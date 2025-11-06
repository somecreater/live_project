package com.live.user.database.mapper;

import com.live.user.database.dto.UserDto;
import com.live.user.database.entity.UsersEntity;

public class UserMapper {

  public UserDto toDto(UsersEntity entity){
    UserDto userDto = new UserDto();
    userDto.setId(entity.getId());
    userDto.setLoginId(entity.getLoginId());
    userDto.setPhone(entity.getPhone());
    userDto.setNickname(entity.getNickname());
    userDto.setLoginType(entity.getLoginType());
    userDto.setUserType(entity.getUserType());
    userDto.setCreatedAt(entity.getCreatedAt());
    userDto.setUpdatedAt(entity.getUpdatedAt());
    return userDto;
  }

  public UsersEntity toEntity(UserDto dto){
    UsersEntity users= new UsersEntity();
    users.setId(dto.getId());
    users.setLoginId(dto.getLoginId());
    users.setPassword(dto.getPassword());
    users.setPhone(dto.getPhone());
    users.setNickname(dto.getNickname());
    users.setLoginType(dto.getLoginType());
    users.setUserType(dto.getUserType());
    users.setCreatedAt(dto.getCreatedAt());
    users.setUpdatedAt(dto.getUpdatedAt());
    return users;
  }

}
