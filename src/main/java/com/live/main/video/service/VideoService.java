package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.video.database.dto.VideoDto;
import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService implements VideoServiceInterface {

  private final S3Presigner s3Presigner;

  @Value("${app.r2.video-bucket-name}")
  private String bucket_name;
  @Value("${app.r2.original-video-bucket-name}")
  private String original_video_folder;
  @Value("${app.r2.transcoding-video-bucket-name}")
  private String transcoding_video_folder;
  @Value("${app.file.video_limit_size}")
  private Long video_limit_size;

  @Override
  @Transactional
  public String VideoUploadUrl(String channel_name, String user_login_id, VideoDto videoDto) {
    if(channel_name.isBlank() || user_login_id.isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(videoDto.getTitle().isBlank() || videoDto.getChannel_name().isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(videoDto.getSize()>video_limit_size
            || videoDto.getFile_type().equals("video/mp4")
            || videoDto.getFile_type().equals("video/mov")){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    String objectKey = original_video_folder + "/" + channel_name + "/" + videoDto.getTitle();
    String contentType = videoDto.getFile_type();

    PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucket_name)
            .key(objectKey)
            .contentType(contentType)
            .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(objectRequest) // URL valid for 15 minutes
            .build();

    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
    return presignedRequest.url().toString();
  }



  @Override
  @Transactional
  public boolean ThumbnailUpload(String channel_name, VideoDto videoDto, MultipartFile file) {
    return true;
  }

  @Override
  @Transactional
  public String VideoPlayUrl(String channel_name, String video_title) {
    return "";
  }

  @Override
  @Transactional
  public boolean VideoDelete(String channel_name, String video_title){
    return true;
  }

  @Override
  @Transactional
  public boolean VideoDeleteOnChannel(String channel_name){
    return true;
  }

}
