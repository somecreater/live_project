package com.live.main.common.database.dto;

import lombok.Getter;

@Getter
public class AlertEvent {
  private final AlertType type;
  private final String publisher;
  private final String content;

  public AlertEvent(String type, String publisher, String content){
    this.type=AlertType.valueOf(type);
    this.publisher=publisher;
    this.content=content;
  }
}
