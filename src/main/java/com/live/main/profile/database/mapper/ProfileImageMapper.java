package com.live.main.profile.database.mapper;

import com.live.main.profile.database.dto.ProfileImageDto;
import com.live.main.profile.database.entity.ProfileImageEntity;
import com.live.main.user.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileImageMapper {
  private final UserRepository userRepository;

  public ProfileImageEntity toEntity(ProfileImageDto profileImageDto){
    return null;
  }

  public ProfileImageDto toDto(ProfileImageEntity profileImageEntity){
    return null;
  }

}
