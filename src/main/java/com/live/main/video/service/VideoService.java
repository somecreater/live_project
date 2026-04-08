package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.video.database.dto.VideoDto;
import com.live.main.video.database.entity.Status;
import com.live.main.video.database.entity.VideoEntity;
import com.live.main.video.database.mapper.VideoMapper;
import com.live.main.video.database.repository.VideoRepository;
import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.FlexibleHashMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
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

  private final VideoMapper videoMapper;
  private final VideoRepository videoRepository;

  public VideoService(
          @Qualifier("r2Presigner") S3Presigner s3Presigner,
          VideoMapper videoMapper,
          VideoRepository videoRepository){
    this.s3Presigner = s3Presigner;
    this.videoMapper = videoMapper;
    this.videoRepository = videoRepository;
  }
  @Override
  @Transactional
  public Map<String, Object> VideoUploadUrl(String channel_name, String user_login_id, VideoDto videoDto) {
    Map<String, Object> result= new HashMap<>();

    if(channel_name.isBlank() || user_login_id.isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(videoDto.getTitle().isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(videoDto.getFile_type().equals("video/mp4") || videoDto.getFile_type().equals("video/mov")){
      log.info(videoDto.getFile_type());
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    try {
      videoDto.setChannel_name(channel_name);
      VideoEntity entity = videoMapper.toEntity(videoDto);
      entity.setStatus(Status.PRIVATE);

      VideoEntity savedEntity = videoRepository.save(entity);

      String objectKey = original_video_folder + channel_name + "/" +
              savedEntity.getId() + "_" + videoDto.getTitle();
      String contentType = normalizeContentType(videoDto.getFile_type());

      log.info("비디오 객체 타입: {}", contentType);
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
      savedEntity.setPresigned_url(presignedRequest.url().toString());
      videoRepository.save(savedEntity);

      result.put("url", presignedRequest.url().toString());
      result.put("video_id", savedEntity.getId());

      return result;

    }catch (Exception e){
      log.error("Error generating presigned URL: {}", e.getMessage());
      throw new CustomException(ErrorCode.SERVER_ERROR);
    }

  }

  @Override
  public String normalizeContentType(String fileType){
    if (fileType == null || fileType.isBlank()) {
      return "application/octet-stream";
    }

    return switch (fileType.toLowerCase()) {
      case "mp4" -> "video/mp4";
      case "mov" -> "video/quicktime";
      case "video/mp4" -> "video/mp4";
      case "video/quicktime" -> "video/quicktime";
      default -> fileType;
    };
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

  @Scheduled(cron = "0 0 0 * * ?")
  @Override
  @Transactional
  public void DeleteUnuploadedVideoInfo(){
    LocalDateTime threshold = LocalDateTime.now().minusHours(1);
    videoRepository.deleteOldPendingVideos(Status.PRIVATE, threshold);
  }

}
