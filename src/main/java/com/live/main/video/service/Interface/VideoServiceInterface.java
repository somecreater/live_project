package com.live.main.video.service.Interface;

import com.live.main.video.database.dto.VideoDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**동영상 기능(2025-12-13)*/
public interface VideoServiceInterface {
  /**동영상 업로드 요청 처리(미리 서명된 URL만 제공)*/
  public Map<String, Object> VideoUploadUrl(String channel_name, String user_login_id, VideoDto videoDto);
  /**동영상 파일 형식 정보 확인 및 변환*/
  public String normalizeContentType(String fileType);
  /**동영상 파일 정보, 파일 검증*/
  public void videoValidation(String channel_name, Long video_id);
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
