package com.live.main.common.database.dto;

import lombok.Getter;

@Getter
public class AlertEvent {
  private final AlertType type;
  private final String sender;
  private final String receiver;
  private final String content;

  public AlertEvent(String type, String sender, String receiver, String content){
    this.type=AlertType.valueOf(type);
    this.sender=sender;
    this.receiver=receiver;
    this.content=content;
  }
}
