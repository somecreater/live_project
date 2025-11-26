package com.live.main.profile.database.dto;

import com.live.main.user.database.entity.UsersEntity;
import lombok.Data;

@Data
public class ProfileImageDto {
  private Long id;
  private String imageName;
  private String imageUrl;
  private int size;
  private String fileType;
  private boolean isUser;
  private Long userId;
}
