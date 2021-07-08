package org.apache.pinot.thirdeye.alert;

import static org.apache.pinot.thirdeye.alert.AlertExceptionHandler.handleAlertEvaluationException;
import static org.apache.pinot.thirdeye.spi.ThirdEyeStatus.ERR_OBJECT_DOES_NOT_EXIST;
import static org.apache.pinot.thirdeye.util.ResourceUtils.ensureExists;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.pinot.thirdeye.detection.v2.plan.DetectionPipelinePlanNodeFactory;
import org.apache.pinot.thirdeye.spi.api.AlertEvaluationApi;
import org.apache.pinot.thirdeye.spi.api.AlertTemplateApi;
import org.apache.pinot.thirdeye.spi.api.DetectionEvaluationApi;
import org.apache.pinot.thirdeye.spi.api.DetectionPlanApi;
import org.apache.pinot.thirdeye.spi.detection.model.DetectionResult;
import org.apache.pinot.thirdeye.spi.detection.v2.DetectionPipelineResult;
import org.apache.pinot.thirdeye.spi.detection.v2.PlanNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AlertEvaluatorV2 {

  public static final String ROOT_OPERATOR_KEY = "root";
  protected static final Logger LOG = LoggerFactory.getLogger(AlertEvaluatorV2.class);

  // 5 detection previews are running at the same time at most
  private static final int PARALLELISM = 5;

  // max time allowed for a preview task
  private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(5);

  private final ExecutorService executorService;
  private final DetectionPipelinePlanNodeFactory detectionPipelinePlanNodeFactory;

  @Inject
  public AlertEvaluatorV2(final DetectionPipelinePlanNodeFactory detectionPipelinePlanNodeFactory) {
    this.detectionPipelinePlanNodeFactory = detectionPipelinePlanNodeFactory;
    executorService = Executors.newFixedThreadPool(PARALLELISM);
  }

  private void stop() {
    executorService.shutdownNow();
  }

  public AlertEvaluationApi evaluate(final AlertEvaluationApi request)
      throws ExecutionException {
    try {
      final Map<String, DetectionPipelineResult> result = runPipeline(request);
      return toApi(result);
    } catch (final Exception e) {
      handleAlertEvaluationException(e);
    }
    return null;
  }

  private Map<String, DetectionPipelineResult> runPipeline(final AlertEvaluationApi request)
      throws Exception {
    ensureExists(request.getAlert(), ERR_OBJECT_DOES_NOT_EXIST, "alert body is null");

    final AlertTemplateApi template = request.getAlert().getTemplate();
    ensureExists(template, ERR_OBJECT_DOES_NOT_EXIST, "alert template body is null");

    final Map<String, PlanNode> pipelinePlanNodes = new HashMap<>();
    for (final DetectionPlanApi operator : template.getNodes()) {
      final String operatorName = operator.getPlanNodeName();
      final PlanNode planNode = detectionPipelinePlanNodeFactory.get(
          operatorName,
          pipelinePlanNodes,
          operator,
          request.getStart().getTime(),
          request.getEnd().getTime());

      pipelinePlanNodes.put(operatorName, planNode);
    }
    return executorService.submit(() -> {
      final Map<String, DetectionPipelineResult> context = new HashMap<>();

      /* Execute the DAG */
      final PlanNode rootNode = pipelinePlanNodes.get(ROOT_OPERATOR_KEY);
      PlanExecutor.executePlanNode(pipelinePlanNodes, context, rootNode);

      /* Return the output */
      return getOutput(context, rootNode);
    }).get(TIMEOUT, TimeUnit.MILLISECONDS);
  }

  private Map<String, DetectionPipelineResult> getOutput(
      final Map<String, DetectionPipelineResult> context,
      final PlanNode rootNode) {
    final Map<String, DetectionPipelineResult> results = new HashMap<>();
    for (final String contextKey : context.keySet()) {
      if (PlanExecutor.getNodeFromContextKey(contextKey).equals(rootNode.getName())) {
        results.put(PlanExecutor.getOutputKeyFromContextKey(contextKey), context.get(contextKey));
      }
    }
    return results;
  }

  private AlertEvaluationApi toApi(final Map<String, DetectionPipelineResult> outputMap) {
    final Map<String, Map<String, DetectionEvaluationApi>> resultMap = new HashMap<>();
    for (final String key : outputMap.keySet()) {
      final DetectionPipelineResult result = outputMap.get(key);
      resultMap.put(key, detectionPipelineResultToApi(result));
    }
    return new AlertEvaluationApi().setEvaluations(resultMap);
  }

  private Map<String, DetectionEvaluationApi> detectionPipelineResultToApi(
      final DetectionPipelineResult result) {
    final Map<String, DetectionEvaluationApi> map = new HashMap<>();
    final List<DetectionResult> detectionResults = result.getDetectionResults();
    for (int i = 0; i < detectionResults.size(); i++) {
      final DetectionEvaluationApi detectionEvaluationApi = detectionResults.get(i).toApi();
      map.put(String.valueOf(i), detectionEvaluationApi);
    }
    return map;
  }
}