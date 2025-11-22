package com.live.main.user.database.dto;

import lombok.Data;

@Data
public class EmailVerification {
  private String email;
  private String token;
}
