package com.live.main.user.database.repository;

import com.live.main.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class EmailVerificationRepository {

  private final RedisService redisService;
  private final String PREFIX="VERIFY_EMAIL";
  public void save(String email, String token){
    redisService.set(PREFIX+email, token, 120, TimeUnit.SECONDS);
  }

  public String get(String email){
    return redisService.get(PREFIX+email);
  }

  public void delete(String email){
      redisService.delete(PREFIX+email);
  }
}
