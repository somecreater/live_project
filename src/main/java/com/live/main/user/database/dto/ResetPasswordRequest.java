package com.live.main.user.database.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
  private String userId;
  private String org_pass;
  private String new_pass;
}
