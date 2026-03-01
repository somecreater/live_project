package com.live.main.user.database.repository;

import com.live.main.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository{

  private final RedisService redisService;
  private static final String PREFIX = "REFRESH:";

  public void save(String userLoginId, String refreshToken, long expirationMillis) {
    redisService.set(PREFIX + userLoginId, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
  }

  public String findByUserId(String userLoginId) {
    return redisService.get(PREFIX + userLoginId);
  }

  public boolean delete(String userLoginId) {
    return redisService.delete(PREFIX + userLoginId);
  }
}
