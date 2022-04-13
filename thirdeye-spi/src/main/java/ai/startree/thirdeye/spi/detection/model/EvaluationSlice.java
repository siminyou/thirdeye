/*
 * Copyright (c) 2022 StarTree Inc. All rights reserved.
 * Confidential and Proprietary Information of StarTree Inc.
 */

package ai.startree.thirdeye.spi.detection.model;

import ai.startree.thirdeye.spi.datalayer.dto.EvaluationDTO;

/**
 * Selector for evaluations based on (optionally) start time and end time.
 */
public class EvaluationSlice {

  private final long start;
  private final long end;

  private EvaluationSlice(long start, long end) {
    this.start = start;
    this.end = end;
  }

  public long getStart() {
    return start;
  }

  public long getEnd() {
    return end;
  }

  public boolean match(EvaluationDTO evaluationDTO) {
    if (this.start >= 0 && evaluationDTO.getEndTime() <= this.start) {
      return false;
    }
    return this.end < 0 || evaluationDTO.getStartTime() < this.end;
  }
}
