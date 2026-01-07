package com.live.main.common.database.repository;

import com.live.main.common.database.dto.AlertEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class AlertRepository {

  private final RedisTemplate<String, AlertEvent> redisTemplate;
  private final String PREFIX="ALERT:";

  public void save(String userId, AlertEvent alertEvent){
    redisTemplate.opsForList().rightPush(PREFIX + userId, alertEvent);
  }

  public List<AlertEvent> get(String userId){
    return redisTemplate.opsForList().range(PREFIX + userId, 0, -1);
  }

  public void delete(String userId){
    redisTemplate.delete(PREFIX + userId);
  }
}
