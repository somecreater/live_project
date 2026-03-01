package com.live.main.common.database.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ManagerMessageEvent {
  private final Long id;
  private final String title;
  private final String content;
  private final String publisher;
  private final String targetId;
  private final boolean read;
  private final LocalDateTime createdAt;

  public ManagerMessageEvent(
    @JsonProperty("id") Long id,
    @JsonProperty("title") String title,
    @JsonProperty("content") String content,
    @JsonProperty("publisher") String publisher,
    @JsonProperty("targetId") String targetId,
    @JsonProperty("read") boolean read,
    @JsonProperty("createdAt") LocalDateTime createdAt) {
      this.id = id;
      this.title = title;
      this.content = content;
      this.publisher = publisher;
      this.targetId = targetId;
      this.read = read;
      this.createdAt = createdAt;
  }
}
