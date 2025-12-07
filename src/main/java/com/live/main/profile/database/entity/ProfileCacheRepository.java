package com.live.main.profile.database.entity;

import com.live.main.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class ProfileCacheRepository {
  private final RedisService redisService;
  private static final String PREFIX="USER_PROFILE_CACHE:";

  public void save(String userId, String url){
    redisService.set(PREFIX+userId, url, 10, TimeUnit.MINUTES);
  }

  public String get(String userId){
    return redisService.get(PREFIX+userId);
  }

  public void delete(String userId){
    redisService.delete(PREFIX+userId);
  }
}
