package com.live.main.user.database.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository{

  private final StringRedisTemplate redisTemplate;

  private static final String PREFIX = "REFRESH:";

  public void save(String userLoginId, String refreshToken, long expirationMillis) {
    redisTemplate.opsForValue()
        .set(PREFIX + userLoginId, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
  }

  public String findByUserId(String userLoginId) {
    return redisTemplate.opsForValue().get(PREFIX + userLoginId);
  }

  public boolean delete(String userLoginId) {
    return redisTemplate.delete(PREFIX + userLoginId);
  }
}
