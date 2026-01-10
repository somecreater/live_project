package com.live.main.common.database.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlertEvent {
  private final Long id;
  private final AlertType type;
  private final String publisher;
  private final String content;
  private final boolean read;

  public AlertEvent(
          @JsonProperty("id") Long id,
          @JsonProperty("type") String type,
          @JsonProperty("publisher") String publisher,
          @JsonProperty("content") String content,
          @JsonProperty("read") boolean read){
    this.id=id;
    this.type=AlertType.valueOf(type);
    this.publisher=publisher;
    this.content=content;
    this.read=read;
  }
}
