package com.live.main.common.database.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AlertType {
  VIDEO_UPLOAD("VIDEO_UPLOAD","NORMAL"),
  STREAMING_START("STREAMING_START","HIGH"),
  POST_UPLOAD("POST_UPLOAD", "NORMAL"),
  REPLY_UPLOAD("REPLY_UPLOAD", "NORMAL"),
  CHANNEL_DELETE("CHANNEL_DELETE", "HIGH");

  private final String type;
  private final String priority;
}
