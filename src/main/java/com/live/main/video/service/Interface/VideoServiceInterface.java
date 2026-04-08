package com.live.main.video.service.Interface;

import com.live.main.video.database.dto.VideoDto;
import org.springframework.web.multipart.MultipartFile;

/**동영상 기능(2025-12-13)*/
public interface VideoServiceInterface {
  /**동영상 업로드 요청 처리(미리 서명된 URL만 제공)*/
  public String VideoUploadUrl(String channel_name, String user_login_id, VideoDto videoDto);
  /**동영상 파일 형식 정보 확인 및 변환*/
  public String normalizeContentType(String fileType);
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
