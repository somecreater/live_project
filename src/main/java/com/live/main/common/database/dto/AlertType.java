package com.live.main.common.database.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertType {
  VIDEO_UPLOAD("CHANNEL","VIDEO_UPLOAD","NORMAL"),
  STREAMING_START("CHANNEL","STREAMING_START","HIGH"),
  STREAMING_STOP("CHANNEL","STREAMING_STOP", "HIGH"),
  POST_UPLOAD("CHANNEL","POST_UPLOAD", "NORMAL"),
  REPLY_UPLOAD("REPLY","REPLY_UPLOAD", "NORMAL"),
  CHANNEL_UPDATE("CHANNEL","CHANNEL_UPDATE", "HIGH"),
  CHANNEL_DELETE("CHANNEL","CHANNEL_DELETE", "HIGH");

  private final String type;
  private final String subtype;
  private final String priority;
}
