package com.live.main.user.database.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
  private int page;
  private int size;
  private String type;
  private String keyword;
}
