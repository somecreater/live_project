package com.live.main.profile.service.Interface;

/**프로필 이미지 기능 2025-11-26*/
public interface ProfileServiceInterface {

  /**프로필 이미지 업로드 기능*/
  public String profile_upload();
  /**프로필 이미지 삭제 기능*/
  public void profile_delete();
  /**프로필 이미지 가져오기(링크) 기능*/
  public String profile_get();
}
