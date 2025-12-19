package com.live.main.video.service.Interface;

import com.live.main.video.database.dto.VideoDto;
import org.springframework.web.multipart.MultipartFile;

/**동영상 기능(2025-12-13)*/
public interface VideoServiceInterface {
  /**동영상 업로드 url 제공 기능*/
  public String VideoUploadUrl(String channel_name, String user_login_id, VideoDto videoDto);
  /**동영상 썸네일 업로드 기능*/
  public boolean ThumbnailUpload(String channel_name, VideoDto videoDto, MultipartFile file);
  /**동영상 재생 url 제공 기능*/
  public String VideoPlayUrl(String channel_name, String video_title);
  /**동영상 삭제 기능*/
  public boolean VideoDelete(String channel_name, String video_title);
  /**동영상 삭제 기능(채널 이름)*/
  public boolean VideoDeleteOnChannel(String channel_name);
}
