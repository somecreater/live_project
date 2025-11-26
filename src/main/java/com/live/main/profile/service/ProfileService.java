package com.live.main.profile.service;

import com.live.main.profile.service.Interface.ProfileServiceInterface;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProfileService implements ProfileServiceInterface {
  private final S3Template s3Template;

  @Override
  public String profile_upload() {
    return "";
  }

  @Override
  public void profile_delete() {

  }

  @Override
  public String profile_get() {
    return "";
  }
}
