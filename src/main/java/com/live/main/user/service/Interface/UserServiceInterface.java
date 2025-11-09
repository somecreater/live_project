package com.live.main.user.service.Interface;

import com.live.main.user.database.dto.UserDto;

public interface UserServiceInterface {
  public UserDto RegisterUser(UserDto userDto);
  public UserDto LoginUser(String id, String pass);
  public UserDto GetUserInfo(UserDto userDto);
  public UserDto UpdateUser(UserDto userDto);
  public boolean UpdatePassword(UserDto userDto, String pass);
  public boolean DeleteUser(UserDto userDto);
}
