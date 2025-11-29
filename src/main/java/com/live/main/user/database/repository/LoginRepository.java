package com.live.main.user.database.repository;

import com.live.main.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class LoginRepository {

  private final RedisService redisService;
  private final String PREFIX="LOGIN_TRY:";

  @Value("${app.login.limit_retry}")
  private int LOGIN_LIMIT_TRY;

  public int save(String login_id){
    String countStr= get(login_id);
    int count= (countStr == null ? 0 : Integer.parseInt(countStr));
    if(count >= LOGIN_LIMIT_TRY){
      return Integer.MAX_VALUE;
    }

    Long newCount= redisService.increment(PREFIX+login_id);
    if(redisService.getExpire(PREFIX+login_id, TimeUnit.SECONDS) == -1){
      long ttl_value= Duration.between(LocalDateTime.now(),
        LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)).getSeconds();
      redisService.expire(PREFIX+login_id,ttl_value,TimeUnit.SECONDS);
    }
    return newCount.intValue();
  }

  public String get(String login_id){
    return redisService.get(PREFIX+login_id);
  }

  public void delete(String login_id){
    redisService.delete(PREFIX+login_id);
  }
}
