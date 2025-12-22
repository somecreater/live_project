package com.live.main.channel.database.dto;

import lombok.Data;

@Data
public class SubscriptionRequest {
  private int page;
  private int size;
  private String name;
  private String keyword;
}
