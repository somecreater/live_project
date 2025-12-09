package com.live.main.profile.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileImageDto {
  private Long id;
  private String imageName;
  private String imageUrl;
  private Long size;
  private String fileType;
  private boolean isUser;
  private Long userId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
