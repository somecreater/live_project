package com.live.main.channel.service.Interface;

import com.live.main.channel.database.dto.CoverDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

/**채널 커버 이미지 기능(2025-12-07)*/
public interface CoverServiceInterface {
  /**채널 커버 이미지 등록 기능*/
  public CoverDto cover_upload(MultipartFile file, String channel_name);
  /**채널 커버 이미지 삭제  기능*/
  public void cover_delete(String channel_name);
  /**채널 커버 이미지 삭제(채널 삭제 시 사용) 기능*/
  public boolean cover_delete_on_channel(String channel_name);
  /**채널 커버 이미지 메타정보 읽기 기능*/
  public CoverDto cover_read_info(String channel_name);
  /**채널 커버 이미지 읽기 기능*/
  public String cover_read(String channel_name);
  /**채널 커버 이미지 다운로드 기능*/
  public InputStreamResource cover_download(String channel_name);

}
