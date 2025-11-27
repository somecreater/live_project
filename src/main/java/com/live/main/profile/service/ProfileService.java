package com.live.main.profile.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.profile.database.dto.ProfileImageDto;
import com.live.main.profile.database.entity.ProfileImageEntity;
import com.live.main.profile.database.mapper.ProfileImageMapper;
import com.live.main.profile.database.repository.ProfileImageRepository;
import com.live.main.profile.service.Interface.ProfileServiceInterface;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.mapper.UserMapper;
import com.live.main.user.service.Interface.UserServiceInterface;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements ProfileServiceInterface {

  private final String PROFILE_FOLDER_NAME="/profile/";
  private final S3Template s3Template;
  @Value("${spring.cloud.aws.s3.bucket}")
  private final String bucket_name;

  private final UserServiceInterface userServiceInterface;
  private final UserMapper userMapper;
  private final ProfileImageRepository profileImageRepository;
  private final ProfileImageMapper profileImageMapper;

    @Override
  public ProfileImageDto profile_upload(MultipartFile file, String fileName, String UserLoginId){
    UserDto userDto=userServiceInterface.GetUserInfo(UserLoginId);
    ProfileImageEntity entity= new ProfileImageEntity();
    ProfileImageDto dto= new ProfileImageDto();

    if(file.isEmpty() || fileName.trim().isEmpty() || userDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    try(InputStream in=file.getInputStream()){
      S3Resource upload= s3Template.upload(bucket_name+PROFILE_FOLDER_NAME,
        fileName,in);
      String uploadURL=upload.getURL().toString();
      entity.setImageName(upload.getFilename());
      entity.setImageUrl(uploadURL);
      entity.setSize(file.getSize());
      entity.setFileType(upload.contentType());
      entity.setUser(true);
      entity.setUsers(userMapper.toEntity(userDto));

      profileImageRepository.save(entity);
      dto=profileImageMapper.toDto(entity);
    } catch (IOException e) {
        throw new CustomException(ErrorCode.NOT_FOUND);
    }
      return dto;
  }

  @Override
  public void profile_delete(String fileName, String UserLoginId){

  }

  @Override
  public ProfileImageDto profile_get(String fileName, String UserLoginId){
    return null;
  }
}
