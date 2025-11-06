package com.live.user.database.dto;

import com.live.user.database.entity.LoginType;
import com.live.user.database.entity.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
  private Long id;
  private String loginId;
  private String password;
  private String phone;
  private String nickname;
  private LoginType loginType;
  private UserType userType;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
