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
    ProfileImageEntity entity=new ProfileImageEntity();
    entity.setId(profileImageDto.getId());
    entity.setImageName(profileImageDto.getImageName());
    entity.setImageUrl(profileImageDto.getImageUrl());
    entity.setSize(profileImageDto.getSize());
    entity.setFileType(profileImageDto.getFileType());
    entity.setUser(profileImageDto.isUser());
    entity.setUsers(userRepository.findById(profileImageDto.getUserId()).orElse(null));
    return entity;
  }

  public ProfileImageDto toDto(ProfileImageEntity profileImageEntity){
    ProfileImageDto dto=new ProfileImageDto();
    dto.setId(profileImageEntity.getId());
    dto.setImageName(profileImageEntity.getImageName());
    dto.setImageUrl(profileImageEntity.getImageUrl());
    dto.setSize(profileImageEntity.getSize());
    dto.setFileType(profileImageEntity.getFileType());
    dto.setUser(profileImageEntity.isUser());
    dto.setUserId(profileImageEntity.getUsers().getId());
    return dto;
  }

}
