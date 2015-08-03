package com.linkedin.thirdeye.anomaly.api;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.linkedin.thirdeye.anomaly.api.external.AnomalyResult;
import com.linkedin.thirdeye.api.DimensionKey;
import com.linkedin.thirdeye.api.StarTreeConfig;

public interface AnomalyResultHandler
{
  /**
   * Initializes this function with the star tree config and arbitrary function config
   */
  void init(StarTreeConfig starTreeConfig, HandlerProperties handlerConfig);

  /**
   * Processes the result of an {@link RuleExpr}
   *
   * <p>
   *   This could be persisting to a database, calling an HTTP endpoint, etc.
   * </p>
   *
   * @throws IOException
   *  If there was an error in handing the result
   */
  void handle(AnomalyDetectionTaskInfo taskInfo, DimensionKey dimensionKey, double dimensionKeyContribution,
      Set<String> metrics, AnomalyResult result) throws IOException;

}
