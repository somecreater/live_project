package com.live.main.channel.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionDto {
  private Long id;
  private String userLoginId;
  private String channelName;
  private LocalDateTime createdAt;
  private boolean notification;
}
