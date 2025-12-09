package com.live.main.user.database.dto;

import lombok.Getter;

@Getter
public class UserDeleteEvent {
  private final String userLoginId;

  public UserDeleteEvent(String userLoginId){
    this.userLoginId=userLoginId;
  }

}
