package com.live.main.channel.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionDto {
  private Long id;
  private String user_login_id;
  private String channel_name;
  private LocalDateTime createdAt;
  private boolean notification;
}
