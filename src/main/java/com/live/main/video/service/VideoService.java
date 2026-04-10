package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.database.dto.VideoValidationEvent;
import com.live.main.common.exception.CustomException;
import com.live.main.video.database.dto.VideoDto;
import com.live.main.video.database.entity.Status;
import com.live.main.video.database.entity.VideoEntity;
import com.live.main.video.database.mapper.VideoMapper;
import com.live.main.video.database.repository.VideoRepository;
import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class VideoService implements VideoServiceInterface {

  private final S3Client s3Client;
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

  @Value("${app.kafka.topic.video-complete.name}")
  private String VIDEO_VALIDATION_TOPIC_NAME;
  private final ApplicationEventPublisher publisher;
  private final KafkaTemplate<String, VideoValidationEvent> kafkaTemplate;

  public VideoService(
          @Qualifier("r2Client") S3Client s3Client,
          @Qualifier("r2Presigner") S3Presigner s3Presigner,
          VideoMapper videoMapper,
          VideoRepository videoRepository,
          ApplicationEventPublisher publisher,
          KafkaTemplate<String, VideoValidationEvent> kafkaTemplate){
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.videoMapper = videoMapper;
    this.videoRepository = videoRepository;
    this.publisher = publisher;
    this.kafkaTemplate = kafkaTemplate;
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

    if(!videoDto.getFile_type().equals("mp4") && !videoDto.getFile_type().equals("mov")){
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
  public boolean videoValidation(String channel_name, Long video_id){
    String object_id=null;

    if(channel_name == null || channel_name.isBlank() || video_id == null) {
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    //DB 내 동영상 정보 검증(검증 실패시 DB, 버킷 내 행, 파일 삭제)
    VideoEntity videoEntity = videoRepository.findById(video_id)
            .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST));
    if(videoEntity.getChannelEntity() == null){
      object_id = original_video_folder + channel_name + "/" +
              video_id + "_" + videoEntity.getTitle();

      deleteObject(object_id);
      videoRepository.delete(videoEntity);
      throw new CustomException(ErrorCode.BAD_REQUEST);

    }
    String savedChannelName = videoEntity.getChannelEntity().getName();

    if (!Objects.equals(savedChannelName, channel_name)) {
      String requestObjectKey = original_video_folder + channel_name + "/" +
              video_id + "_" + videoEntity.getTitle();
      deleteObject(requestObjectKey);

      String dbObjectKey = original_video_folder + savedChannelName + "/" +
              video_id + "_" + videoEntity.getTitle();
      deleteObject(dbObjectKey);

      videoRepository.delete(videoEntity);
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    object_id = original_video_folder + channel_name + "/" + video_id + "_" + videoEntity.getTitle();
    log.info("동영상 1차 검증 완료 - video_id: {}, key: {}", video_id, object_id);

    try {

      HeadObjectRequest request = HeadObjectRequest.builder()
              .bucket(bucket_name)
              .key(object_id)
              .build();
      HeadObjectResponse response= s3Client.headObject(request);

      //파일 크기 검증
      if(response.contentLength() == null || response.contentLength() <= 0){
        deleteObject(object_id);
        videoRepository.delete(videoEntity);
        throw new CustomException(ErrorCode.BAD_REQUEST);
      }
      if(response.contentLength() < 1024){
        deleteObject(object_id);
        videoRepository.delete(videoEntity);
        throw new CustomException(ErrorCode.BAD_REQUEST);
      }

      //파일 내용 검사
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
              .bucket(bucket_name)
              .key(object_id)
              .range("bytes=" + 0 + "-" + 4095)
              .build();

      ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
      byte[] headerBytes= responseBytes.asByteArray();
      if(!isValidMp4OrMov(headerBytes)){
        deleteObject(object_id);
        videoRepository.delete(videoEntity);
        throw new CustomException(ErrorCode.BAD_REQUEST);
      }

      log.info("동영상 2차 검증 완료 - video_id: {}, key: {}", video_id, object_id);
      publisher.publishEvent(new VideoValidationEvent(video_id, object_id));

      return true;

    } catch (NoSuchKeyException e) {
      log.error("R2 객체 없음 - key: {}", object_id, e);
      throw new CustomException(ErrorCode.BAD_REQUEST);
    } catch (S3Exception e){
      String errorMessage = e.awsErrorDetails() != null
              ? e.awsErrorDetails().errorMessage()
              : e.getMessage();
      log.error("R2 파일 검증 실패 - key: {}, message: {}", object_id, errorMessage, e);
      throw new RuntimeException("R2 파일 검증 실패", e);
    }
  }

  @Async("IOTaskExecutor")
  @EventListener
  @Override
  public void publishVideoValidationCompleted(VideoValidationEvent event){
    kafkaTemplate.send(
      VIDEO_VALIDATION_TOPIC_NAME,
      event.getObject_key(),
      event
    );

    log.info("Kafka Video Validation Message Produced to Kafka - Object KEY: {}, Video ID: {}",
            event.getObject_key(),
            event.getVideo_id());
  }

  @Override
  public boolean isValidMp4OrMov(byte[] bytes){
    if (bytes == null || bytes.length < 12) {
      return false;
    }

    int max= Math.min(bytes.length - 8, 256);
    for (int i = 0; i < max; i++){
      if((bytes[i] == 'f'
              && bytes[i + 1] == 't'
              && bytes[i + 2] == 'y'
              && bytes[i + 3] == 'p')){

        if (i + 8 <= bytes.length) {
          String brand = new String(bytes, i + 4, 4, StandardCharsets.US_ASCII).trim();

          return brand.equals("isom")
                  || brand.equals("iso2")
                  || brand.equals("mp41")
                  || brand.equals("mp42")
                  || brand.equals("avc1")
                  || brand.equals("M4V ")
                  || brand.equals("MSNV")
                  || brand.equals("qt  ");
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public void deleteObject(String objectKey){
    try {
      DeleteObjectRequest request = DeleteObjectRequest.builder()
              .bucket(bucket_name)
              .key(objectKey)
              .build();
      s3Client.deleteObject(request);
      log.info("R2 객체 삭제 완료: {}", objectKey);

    } catch (S3Exception e) {
      String errorMessage = e.awsErrorDetails() != null
              ? e.awsErrorDetails().errorMessage()
              : e.getMessage();

      log.error("R2 객체 삭제 실패 - key: {}, message: {}", objectKey, errorMessage, e);
      throw new RuntimeException("R2 객체 삭제 실패", e);
    } catch (Exception e) {
      log.error("R2 객체 삭제 중 예외 발생 - key: {}", objectKey, e);
      throw new RuntimeException("R2 객체 삭제 중 예외 발생", e);
    }
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
