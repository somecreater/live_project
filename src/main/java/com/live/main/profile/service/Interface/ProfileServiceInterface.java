package com.live.main.profile.service.Interface;

import com.live.main.profile.database.dto.ProfileImageDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

/**프로필 이미지 기능(2025-11-26)*/
public interface ProfileServiceInterface {

  /**프로필 이미지 업로드 기능*/
  public ProfileImageDto profile_upload(MultipartFile file, String userLoginId);
  /**프로필 이미지 삭제 기능*/
  public void profile_delete(String userLoginId);
  /**프로필 이미지 삭제 기능(회원 탈퇴시 사용)*/
  public boolean profile_delete_onUser(String user_login_ud);
  /**프로필 이미지 가져오기(객체) 기능*/
  public ProfileImageDto profile_get(String userLoginId);
  /**프로필 이미지 가져오기(파일) 기능*/
  public InputStreamResource profile_download(String userLoginId);
  /**프로필 이미지 링크 가져오기 기능*/
  public String profile_read(String userLoginId);
}
