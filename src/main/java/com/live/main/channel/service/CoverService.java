package com.live.main.channel.service;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.CoverDto;
import com.live.main.channel.database.entity.CoverEntity;
import com.live.main.channel.database.mapper.ChannelMapper;
import com.live.main.channel.database.mapper.CoverMapper;
import com.live.main.channel.database.repository.CoverRepository;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.CoverServiceInterface;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.common.service.Interface.CommonServiceInterface;
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
public class CoverService implements CoverServiceInterface {

  private final S3Template s3Template;
  private final S3Presigner s3Presigner;
  @Value("${spring.cloud.aws.s3.bucket}")
  private String bucket_name;
  @Value("${app.aws.bucket-name-cover}")
  private String coverFolderName;
  @Value("${app.file.profile_limit-size}")
  private Long profileLimitSize;

  private final ChannelMapper channelMapper;
  private final CoverMapper coverMapper;
  private final CoverRepository coverRepository;
  private final ChannelServiceInterface channelService;
  private final CommonServiceInterface commonService;

  @Transactional
  @Override
  public CoverDto cover_upload(MultipartFile file, String channel_name) {
    ChannelDto channelDto=channelService.getChannelInfoByName(channel_name);
    CoverEntity old_cover= coverRepository.findByChannel_Name(channel_name).orElse(null);
    if(channelDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(!commonService.isSafeFile(file)||file.getSize()>profileLimitSize){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    String uuid= UUID.randomUUID().toString().replaceAll("-","");
    String filaName= uuid+"_"+channelDto.getName()+"_cover";
    try(InputStream in=file.getInputStream()){
      String key= coverFolderName+channelDto.getName()+"/"+filaName;
      ObjectMetadata metadata= ObjectMetadata.builder()
        .contentType(file.getContentType())
        .contentLength(file.getSize()).build();

      S3Resource upload= s3Template.upload(bucket_name, key, in, metadata);
      if(!upload.exists()){
        log.info("{} 채널의 커버 이미지 업로드 실패", channelDto.getName());
        throw new CustomException(ErrorCode.NOT_FOUND);
      }

      String url=upload.getURL().toString();
      if(old_cover != null){
        log.info("{} 채널의 커버 파일이 존재(데이터 수정)", channel_name);

        String oldFileName= old_cover.getImage_name();
        s3Template.deleteObject(bucket_name,oldFileName);

        old_cover.setImage_name(upload.getFilename());
        old_cover.setImage_url(url);
        old_cover.setSize(file.getSize());
        old_cover.setFile_type(upload.contentType());
        old_cover.setUpdatedAt(LocalDateTime.now());
        coverRepository.save(old_cover);

        return coverMapper.toDto(old_cover);
      }else{
        CoverEntity coverEntity= new CoverEntity();
        coverEntity.setImage_name(upload.getFilename());
        coverEntity.setImage_url(url);
        coverEntity.setSize(file.getSize());
        coverEntity.setFile_type(upload.contentType());
        coverEntity.setChannel(channelMapper.toEntity(channelDto));
        coverEntity.setUpdatedAt(LocalDateTime.now());
        coverEntity.setCreatedAt(LocalDateTime.now());
        coverRepository.save(coverEntity);

        return coverMapper.toDto(coverEntity);
      }
    }catch (IOException e){
      e.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Transactional
  @Override
  public void cover_delete(String channel_name) {
    try{
      CoverEntity coverEntity= coverRepository.findByChannel_Name(channel_name).orElse(null);
      if(coverEntity == null){
        throw new CustomException(ErrorCode.NOT_FOUND);
      }
      String fileName= coverEntity.getImage_name();
      s3Template.deleteObject(bucket_name,fileName);
      coverRepository.deleteById(coverEntity.getId());

    } catch (S3Exception s3) {
      s3.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }

  @Transactional
  @Override
  public boolean cover_delete_on_channel(String channel_name) {
    try {
      CoverEntity entity = coverRepository.findByChannel_Name(channel_name).orElse(null);
      if (entity == null) return true;

      String fileName = entity.getImage_name();
      s3Template.deleteObject(bucket_name, fileName);
      coverRepository.deleteById(entity.getId());
      return true;
    } catch (S3Exception s3) {
      s3.printStackTrace();
      return false;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public CoverDto cover_read_info(String channel_name) {
    CoverEntity entity= coverRepository.findByChannel_Name(channel_name).orElse(null);
    if(entity !=null){
      return coverMapper.toDto(entity);
    } else{
      return null;
    }
  }

  @Transactional(readOnly = true)
  @Override
  public String cover_read(String channel_name) {
    CoverEntity entity= coverRepository.findByChannel_Name(channel_name).orElse(null);
    if (entity == null) {
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
    String fileName= entity.getImage_name();
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
              .bucket(bucket_name)
              .key(fileName).build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofDays(1))
              .getObjectRequest(getObjectRequest).build();

    PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);

    return presignedGetObjectRequest.url().toString();
  }

  @Transactional(readOnly = true)
  @Override
  public InputStreamResource cover_download(String channel_name) {
    try{
      CoverEntity entity= coverRepository.findByChannel_Name(channel_name).orElse(null);
      if(entity == null){
        throw new CustomException(ErrorCode.NOT_FOUND);
      }
      String fileName= entity.getImage_name();
      S3Resource s3Resource=s3Template.download(bucket_name,fileName);
      InputStream in=s3Resource.getInputStream();
      return new InputStreamResource(in);

    } catch (S3Exception | IOException s3) {
      s3.printStackTrace();
      throw new CustomException(ErrorCode.NOT_FOUND);
    }
  }
}
