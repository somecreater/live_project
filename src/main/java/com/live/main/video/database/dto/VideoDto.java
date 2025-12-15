package com.live.main.video.database.dto;

import com.live.main.video.database.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoDto {
  private Long id;
  private String title;
  private String description;
  private String file_type;
  private Long size;
  private boolean visibility;
  private boolean allow_comments;
  private Status status;
  private int duration_seconds;
  private int like;
  private int unlike;
  private int view_count;
  private Long channel_id;
  private String channel_name;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
