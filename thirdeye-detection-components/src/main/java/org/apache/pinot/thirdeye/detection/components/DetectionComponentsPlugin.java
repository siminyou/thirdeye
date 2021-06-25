package org.apache.pinot.thirdeye.detection.components;

import com.google.common.collect.ImmutableList;
import org.apache.pinot.thirdeye.detection.components.detectors.AbsoluteChangeRuleDetector;
import org.apache.pinot.thirdeye.detection.components.detectors.AbsoluteChangeRuleDetectorSpec;
import org.apache.pinot.thirdeye.detection.components.detectors.DataSlaQualityChecker;
import org.apache.pinot.thirdeye.detection.components.detectors.DataSlaQualityCheckerSpec;
import org.apache.pinot.thirdeye.detection.components.detectors.HoltWintersDetector;
import org.apache.pinot.thirdeye.detection.components.detectors.HoltWintersDetectorSpec;
import org.apache.pinot.thirdeye.detection.components.detectors.MeanVarianceRuleDetector;
import org.apache.pinot.thirdeye.detection.components.detectors.MeanVarianceRuleDetectorSpec;
import org.apache.pinot.thirdeye.detection.components.detectors.PercentageChangeRuleDetector;
import org.apache.pinot.thirdeye.detection.components.detectors.PercentageChangeRuleDetectorSpec;
import org.apache.pinot.thirdeye.detection.components.detectors.PercentageChangeRuleDetectorV2;
import org.apache.pinot.thirdeye.detection.components.detectors.ThresholdRuleDetector;
import org.apache.pinot.thirdeye.detection.components.detectors.ThresholdRuleDetectorSpec;
import org.apache.pinot.thirdeye.detection.components.detectors.ThresholdRuleDetectorV2;
import org.apache.pinot.thirdeye.spi.Plugin;
import org.apache.pinot.thirdeye.spi.detection.AnomalyDetectorFactory;
import org.apache.pinot.thirdeye.spi.detection.AnomalyDetectorV2Factory;

public class DetectionComponentsPlugin implements Plugin {

  @Override
  public Iterable<AnomalyDetectorFactory> getAnomalyDetectorFactories() {
    return ImmutableList.of(
        new GenericAnomalyDetectorFactory<>(
            "ABSOLUTE_CHANGE_RULE",
            AbsoluteChangeRuleDetectorSpec.class,
            AbsoluteChangeRuleDetector.class
        ),
        new GenericAnomalyDetectorFactory<>(
            "HOLT_WINTERS_RULE",
            HoltWintersDetectorSpec.class,
            HoltWintersDetector.class
        ),
        new GenericAnomalyDetectorFactory<>(
            "MEAN_VARIANCE_RULE",
            MeanVarianceRuleDetectorSpec.class,
            MeanVarianceRuleDetector.class
        ),
        new GenericAnomalyDetectorFactory<>(
            "PERCENTAGE_RULE",
            PercentageChangeRuleDetectorSpec.class,
            PercentageChangeRuleDetector.class
        ),
        new GenericAnomalyDetectorFactory<>(
            "THRESHOLD",
            ThresholdRuleDetectorSpec.class,
            ThresholdRuleDetector.class
        ),
        new GenericAnomalyDetectorFactory<>(
            "DATA_SLA",
            DataSlaQualityCheckerSpec.class,
            DataSlaQualityChecker.class
        )
    );
  }

  @Override
  public Iterable<AnomalyDetectorV2Factory> getAnomalyDetectorV2Factories() {
    return ImmutableList.of(
        new GenericAnomalyDetectorV2Factory<>(
            "PERCENTAGE_RULE",
            PercentageChangeRuleDetectorSpec.class,
            PercentageChangeRuleDetectorV2.class
        ),
        new GenericAnomalyDetectorV2Factory<>(
            "THRESHOLD_RULE",
            ThresholdRuleDetectorSpec.class,
            ThresholdRuleDetectorV2.class
        )
    );
  }
}