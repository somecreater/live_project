package com.live.main.profile.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.common.service.Interface.CommonServiceInterface;
import com.live.main.profile.database.dto.ProfileImageDto;
import com.live.main.profile.database.entity.ProfileCacheRepository;
import com.live.main.profile.database.entity.ProfileImageEntity;
import com.live.main.profile.database.mapper.ProfileImageMapper;
import com.live.main.profile.database.repository.ProfileImageRepository;
import com.live.main.profile.service.Interface.ProfileServiceInterface;
import com.live.main.user.database.dto.UserDeleteEvent;
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
import org.springframework.context.event.EventListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements ProfileServiceInterface {

  private final S3Template s3Template;
  private final S3Presigner s3Presigner;
  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket_name;
  @Value("${app.aws.bucket-name-profile}")
  private String profileFolderName;
  @Value("${app.file.profile_limit-size}")
  private Long profileLimitSize;
  private final CommonServiceInterface commonService;
  private final UserServiceInterface userServiceInterface;
  private final UserMapper userMapper;
  private final ProfileImageRepository profileImageRepository;
  private final ProfileCacheRepository profileCacheRepository;
  private final ProfileImageMapper profileImageMapper;

  @Override
  @Transactional
  public ProfileImageDto profile_upload(MultipartFile file, String userLoginId){
    UserDto userDto=userServiceInterface.GetUserInfo(userLoginId);
    ProfileImageEntity old_entity=profileImageRepository.findByUsers_LoginId(userLoginId).orElse(null);
    ProfileImageEntity entity= new ProfileImageEntity();

    if(userDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(!commonService.isSafeFile(file)||file.getSize()>profileLimitSize){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    String uuid= UUID.randomUUID().toString().replaceAll("-","");
    String fileName=uuid+"_"+userDto.getLoginId()+"_profile";
    try(InputStream in=file.getInputStream()){
      String key = profileFolderName +userDto.getLoginId()+"/"+fileName;
      ObjectMetadata metadata= ObjectMetadata.builder()
        .contentType(file.getContentType())
        .contentLength(file.getSize()).build();

      S3Resource upload= s3Template.upload(bucket_name, key, in, metadata);
      if(!upload.exists()){
        log.info("{}님의 프로필 이미지 업로드 실패", userDto.getLoginId());
        throw new CustomException(ErrorCode.NOT_FOUND);
      }

      String url=upload.getURL().toString();
      if(old_entity != null){
        log.info("{} 님의 프로필 파일이 존재(데이터 수정)", userLoginId);

        String oldFileName=  old_entity.getImageName();
        s3Template.deleteObject(bucket_name, oldFileName);
        profileCacheRepository.delete(userLoginId);

        old_entity.setImageName(upload.getFilename());
        old_entity.setImageUrl(url);
        old_entity.setSize(file.getSize());
        old_entity.setFileType(upload.contentType());
        old_entity.setUpdatedAt(LocalDateTime.now());
        profileCacheRepository.delete(userLoginId);
        profileImageRepository.save(old_entity);

        return profileImageMapper.toDto(old_entity);
      }else{
        entity.setImageName(upload.getFilename());
        entity.setImageUrl(url);
        entity.setSize(file.getSize());
        entity.setFileType(upload.contentType());
        entity.setUser(true);
        entity.setUsers(userMapper.toEntity(userDto));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        profileImageRepository.save(entity);

        return profileImageMapper.toDto(entity);
      }

    } catch (IOException e) {
        throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Async
  @Override
  @Transactional
  @EventListener
  public void profile_delete_onUser(UserDeleteEvent event){
    try{
      ProfileImageEntity entity= profileImageRepository.findByUsers_LoginId(event.getUserLoginId()).orElse(null);
      if(entity == null) return;

      String fileName=  entity.getImageName();
      s3Template.deleteObject(bucket_name, fileName);
      profileImageRepository.deleteById(entity.getId());
      profileCacheRepository.delete(event.getUserLoginId());

      }catch (S3Exception s3){
        s3.printStackTrace();
        throw new CustomException(ErrorCode.NOT_FOUND);
      }

  }

  @Override
  @Transactional
  public void profile_delete(String userLoginId){
    try{
      ProfileImageEntity entity= profileImageRepository.findByUsers_LoginId(userLoginId).orElse(null);
      if(entity == null){
        throw new CustomException(ErrorCode.NOT_FOUND);
      }

      String fileName=  entity.getImageName();
      s3Template.deleteObject(bucket_name, fileName);
      profileImageRepository.deleteById(entity.getId());
      profileCacheRepository.delete(userLoginId);
    }catch (S3Exception s3){
      s3.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ProfileImageDto profile_get(String userLoginId){
    ProfileImageEntity entity= profileImageRepository.findByUsers_LoginId(userLoginId).orElse(null);
    if(entity != null) {
      return profileImageMapper.toDto(entity);
    }else{
      return null;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public InputStreamResource profile_download(String userLoginId){
    try{
      ProfileImageEntity profileImageEntity=profileImageRepository
        .findByUsers_LoginId(userLoginId).orElse(null);
      if(profileImageEntity == null){
          throw new CustomException(ErrorCode.NOT_FOUND);
      }

      String fileName=  profileImageEntity.getImageName();
      S3Resource s3Resource=s3Template.download(bucket_name,fileName);
      InputStream in=s3Resource.getInputStream();
      return new InputStreamResource(in);
    }catch (S3Exception | IOException s3){
      s3.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Override
  @Transactional
  public String profile_read(String userLoginId) {
    String cache_url = profileCacheRepository.get(userLoginId);
    if (cache_url != null) {
      return cache_url;
    } else {
      String fileName;
      ProfileImageEntity profileImageEntity = profileImageRepository
                  .findByUsers_LoginId(userLoginId).orElse(null);
      if (profileImageEntity == null) {
        throw new CustomException(ErrorCode.NOT_FOUND);
      }

      fileName = profileImageEntity.getImageName();

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                  .bucket(bucket_name)
                  .key(fileName).build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofDays(1))
                  .getObjectRequest(getObjectRequest).build();

      PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
      String preSignedUrl=presignedGetObjectRequest.url().toString();

      profileCacheRepository.save(userLoginId,preSignedUrl);
      return preSignedUrl;
    }
  }
}
