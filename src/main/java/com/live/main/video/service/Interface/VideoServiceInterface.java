package com.live.main.video.service.Interface;

import com.live.main.common.database.dto.VideoValidationEvent;
import com.live.main.video.database.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**동영상 기능(2025-12-13)*/
public interface VideoServiceInterface {
  /**동영상 업로드 요청 처리(미리 서명된 URL만 제공)*/
  public Map<String, Object> VideoUploadUrl(String channel_name, String user_login_id, VideoDto videoDto);

  /**대용량 동영상 업로드를 위한 multipart 업로드 세션 생성 요청*/
  public MultipartUploadRequest createMultipartUploadSession(String channel_name, VideoDto videoDto);
  /**각 multipart 업로드에 대한 URL 생성*/
  public List<PartPresignedUrlResponse> presignUploadParts(PresignPartsRequest presignPartsRequest);
  /**multipart 업로드시 part 넘버 검증*/
  public void validatePartNumbers(List<Integer> partNumbers, int totalPartCount);
  /**multipart 업로드 완료 요청*/
  public void completeMultipartUpload(CompleteUploadRequest request);
  /**multipart 업로드 중단 요청(취소, 실패시)*/
  public void abortUpload(AbortUploadRequest request);

  /**동영상 파일 형식 정보 확인 및 변환*/
  public String normalizeContentType(String fileType);
  /**동영상 파일 정보, 파일 검증*/
  public boolean videoValidation(String channel_name, Long video_id);
  /**Kafka에 검증 완료 메시지 삽입*/
  public void publishVideoValidationCompleted(VideoValidationEvent event);
  /**동영상 파일 내용 검사*/
  public boolean isValidMp4OrMov(byte[] bytes);
  /**R2 내 저장된 파일 삭제*/
  public void deleteObject(String object_key);

  /**동영상 썸네일 업로드 기능*/
  public boolean ThumbnailUpload(String channel_name, VideoDto videoDto, MultipartFile file);
  /**동영상 재생 url 제공 기능*/
  public String VideoPlayUrl(String channel_name, String video_title);

  /**동영상 삭제 기능*/
  public boolean VideoDelete(String channel_name, String video_title);
  /**동영상 삭제 기능(채널 이름)*/
  public boolean VideoDeleteOnChannel(String channel_name);
  /**미업로드 상태의 동영상 정보 삭제 기능*/
  public void DeleteUnuploadedVideoInfo();
}
