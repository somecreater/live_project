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
public class EmailVerificationRepository {

  private final RedisService redisService;
  private final String PREFIX="VERIFY_EMAIL:";
  private final String LIMIT_PREFIX="LIMIT_VERIFY_EMAIL:";

  @Value("${app.verification-limit}")
  private int LIMIT_EMAIL_NUM;

  public void save(String email, String token){
    redisService.set(PREFIX+email, token, 120, TimeUnit.SECONDS);
  }

  public String get(String email){
    return redisService.get(PREFIX+email);
  }

  public void delete(String email){
      redisService.delete(PREFIX+email);
  }

  public int limit_save(String email){
    String countStr= limit_get(email);
    int count= (countStr == null ? 0 : Integer.parseInt(countStr));

    if(count == LIMIT_EMAIL_NUM){
      return Integer.MAX_VALUE;
    }

    Long newCount=redisService.increment(LIMIT_PREFIX+email);
    if(redisService.getExpire(LIMIT_PREFIX+email, TimeUnit.SECONDS) == -1){
      long ttl_value= Duration.between(LocalDateTime.now(),
        LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)).getSeconds();
      redisService.expire(LIMIT_PREFIX+email,ttl_value,TimeUnit.SECONDS);
    }
    return newCount.intValue();
  }

  public String limit_get(String email){
    return redisService.get(LIMIT_PREFIX+email);
  }

  public void limit_delete(String email){
    redisService.delete(LIMIT_PREFIX+email);
  }
}
