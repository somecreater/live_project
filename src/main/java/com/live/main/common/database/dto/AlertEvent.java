package com.live.main.common.database.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlertEvent {
  private final AlertType type;
  private final String publisher;
  private final String content;

  public AlertEvent(
          @JsonProperty("type") String type,
          @JsonProperty("publisher") String publisher,
          @JsonProperty("content") String content){
    this.type=AlertType.valueOf(type);
    this.publisher=publisher;
    this.content=content;
  }
}
