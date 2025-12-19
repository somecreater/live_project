package com.live.main.channel.database.dto;

import lombok.Data;

@Data
public class PostDeleteRequest {
  private String channel_name;
  private Long post_id;
}
