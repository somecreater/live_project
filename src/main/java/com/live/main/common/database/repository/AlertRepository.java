package com.live.main.common.database.repository;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class AlertRepository {

  private final RedisService redisService;
  private final String PREFIX="ALERT:";

  public void save(String userId, AlertEvent alertEvent){
      redisService.rPush(PREFIX+userId, alertEvent);
  }

  public List<AlertEvent> get(String userId){
    List<Object> alertEvents= redisService.lGet(PREFIX+userId);
    if(alertEvents == null){
      return null;
    }else{
      return alertEvents.stream().map(o -> (AlertEvent) o).toList();
    }
  }

  public void delete(String userId){
    redisService.delete(PREFIX+userId);
  }
}
