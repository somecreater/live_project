package com.live.main.user.database.mapper;

import com.live.main.user.database.dto.RefreshTokenDto;
import com.live.main.user.database.entity.RefreshTokenEntity;
import com.live.main.user.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenMapper {

  private final UserRepository userRepository;

  public RefreshTokenEntity toEntity(RefreshTokenDto Dto){
    RefreshTokenEntity entity=new RefreshTokenEntity();
    entity.setId(Dto.getId());
    entity.setToken(Dto.getToken());
    entity.setExpiryDate(Dto.getExpiryDate());
    entity.setLoginId(Dto.getUserLoginId());
    entity.setAuth(Dto.getAuth());
    return entity;
  }

  public RefreshTokenDto toDto(RefreshTokenEntity entity){
    RefreshTokenDto dto=new RefreshTokenDto();
    dto.setId(entity.getId());
    dto.setToken(entity.getToken());
    dto.setExpiryDate(entity.getExpiryDate());
    dto.setUserLoginId(entity.getLoginId());
    dto.setAuth(entity.getAuth());
    return dto;
  }
}
