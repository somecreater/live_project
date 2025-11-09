package com.live.main.user.database.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshTokenDto {
  private Long id;
  private String token;
  private String auth;
  private Instant expiryDate;
  private String userLoginId;
}
