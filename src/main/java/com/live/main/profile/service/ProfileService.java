package com.live.main.profile.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.common.service.Interface.CommonServiceInterface;
import com.live.main.profile.database.dto.ProfileImageDto;
import com.live.main.profile.database.entity.ProfileImageEntity;
import com.live.main.profile.database.mapper.ProfileImageMapper;
import com.live.main.profile.database.repository.ProfileImageRepository;
import com.live.main.profile.service.Interface.ProfileServiceInterface;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.mapper.UserMapper;
import com.live.main.user.service.Interface.UserServiceInterface;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements ProfileServiceInterface {

  private final S3Template s3Template;
  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket_name;
  @Value("${app.aws.bucket-name-profile}")
  private String profileFolderName;
  private final CommonServiceInterface commonService;
  private final UserServiceInterface userServiceInterface;
  private final UserMapper userMapper;
  private final ProfileImageRepository profileImageRepository;
  private final ProfileImageMapper profileImageMapper;

  @Override
  @Transactional
  public ProfileImageDto profile_upload(MultipartFile file, String fileName, String UserLoginId){
    UserDto userDto=userServiceInterface.GetUserInfo(UserLoginId);
    ProfileImageEntity entity= new ProfileImageEntity();

    if(fileName == null || fileName.trim().isEmpty() || userDto == null ){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(commonService.isSafeFile(file)){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    try(InputStream in=file.getInputStream()){
      String key = profileFolderName +"/"+userDto.getLoginId()+"/"+fileName;
      ObjectMetadata metadata= ObjectMetadata.builder()
        .contentType(file.getContentType())
        .contentLength(file.getSize()).build();

      S3Resource upload= s3Template.upload(bucket_name, key, in, metadata);

      String url=upload.getURL().toString();
      entity.setImageName(upload.getFilename());
      entity.setImageUrl(url);
      entity.setSize(file.getSize());
      entity.setFileType(upload.contentType());
      entity.setUser(true);
      entity.setUsers(userMapper.toEntity(userDto));

      profileImageRepository.save(entity);
      return profileImageMapper.toDto(entity);
    } catch (IOException e) {
        throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Override
  @Transactional
  public void profile_delete(String fileName, String UserLoginId){
    try{
      ProfileImageEntity entity= profileImageRepository.findByImageName(fileName).orElse(null);
      if(entity == null){
        throw new CustomException(ErrorCode.NOT_FOUND);
      }
      String key =profileFolderName+"/"+UserLoginId+"/"+fileName;
      s3Template.deleteObject(bucket_name, key);
      profileImageRepository.delete(entity);

    }catch (S3Exception s3){
      s3.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ProfileImageDto profile_get(String fileName, String UserLoginId){
    ProfileImageEntity entity= profileImageRepository.findByImageName(fileName).orElse(null);
    if(entity != null) {
      return profileImageMapper.toDto(entity);
    }else{
      return null;
    }
  }

  public InputStreamResource profile_download(String fileName, String UserLoginId){
    try{
      String key =profileFolderName+"/"+UserLoginId+"/"+fileName;
      S3Resource s3Resource=s3Template.download(bucket_name,key);
      InputStream in=s3Resource.getInputStream();
      return new InputStreamResource(in);
    }catch (S3Exception | IOException s3){
      s3.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }
}
