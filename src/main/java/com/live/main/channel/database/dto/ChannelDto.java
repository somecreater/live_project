package com.live.main.channel.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChannelDto {
  private Long id;
  private String name;
  private String description;
  private Long subscription_count;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long user_id;
  private String user_login_id;
}
