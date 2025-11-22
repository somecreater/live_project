package com.live.main.user.database.dto;

import lombok.Data;

@Data
public class RequestPassword {
  private String LoginId;
  private String email;
}
