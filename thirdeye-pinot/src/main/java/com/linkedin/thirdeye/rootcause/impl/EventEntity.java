package com.linkedin.thirdeye.rootcause.impl;

import com.linkedin.thirdeye.anomaly.events.EventType;
import com.linkedin.thirdeye.datalayer.dto.EventDTO;
import com.linkedin.thirdeye.rootcause.Entity;


/**
 * EventEntity represents an individual event. It holds meta-data referencing ThirdEye's internal
 * database. The URN namespace is defined as 'thirdeye:event:{type}:{name}:{start}'.
 */
public class EventEntity extends Entity {
  private static final EntityType TYPE = new EntityType("thirdeye:event:");

  public static EventEntity fromDTO(double score, EventDTO dto) {
    String urn = TYPE.formatURN(dto.getEventType(), dto.getName(), dto.getStartTime());
    return new EventEntity(urn, score, dto);
  }

  final EventDTO dto;

  protected EventEntity(String urn, double score, EventDTO dto) {
    super(urn, score);
    this.dto = dto;
  }

  public EventDTO getDto() {
    return dto;
  }
}
