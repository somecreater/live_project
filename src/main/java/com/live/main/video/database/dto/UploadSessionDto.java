package com.live.main.video.database.dto;

import com.live.main.video.database.entity.UploadSessionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadSessionDto {
  private Long id;
  private String uploadId;
  private Long videoId;
  private String objectKey;
  private UploadSessionStatus status;
  private Integer totalPartCount;
  private Integer completedPartCount;
  private Long partSize;
  private LocalDateTime completedAt;
  private LocalDateTime abortedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
