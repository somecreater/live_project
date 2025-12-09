package com.live.main.channel.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoverDto {
  private Long id;
  private String image_name;
  private String image_url;
  private Long size;
  private String file_type;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long channel_id;
  private String channel_name;
}
