package com.live.main.channel.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
  private Long id;
  private String title;
  private String content;
  private String category;
  private Long like;
  private Long unlike;
  private boolean visibility;
  private boolean commentable;
  private Long channel_id;
  private String channel_name;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
