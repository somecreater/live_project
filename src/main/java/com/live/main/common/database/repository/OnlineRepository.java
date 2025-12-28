package com.live.main.common.database.repository;


import com.live.main.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class OnlineRepository {

  private final RedisService redisService;
  private final String PREFIX="ONLINE:";
  private static final Long ONLINE_TTL=60L;

  public void save(String loginId, String sessionId){
    redisService.set(PREFIX+loginId,sessionId, ONLINE_TTL, TimeUnit.SECONDS);
  }

  public void extend(String loginId){
    redisService.expire(PREFIX+loginId, ONLINE_TTL, TimeUnit.SECONDS);
  }

  public String get(String loginId){
    return redisService.get(PREFIX+loginId);
  }

  public void delete(String loginId){
    redisService.delete(loginId);
  }
}
