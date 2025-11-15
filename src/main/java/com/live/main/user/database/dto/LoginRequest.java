package com.live.main.user.database.dto;

import lombok.Data;

@Data
public class LoginRequest {
  private String loginId;
  private String pass;
}
