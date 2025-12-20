package com.live.main.channel.database.mapper;

import com.live.main.channel.database.dto.SubscriptionDto;
import com.live.main.channel.database.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper {

  public SubscriptionEntity toEntity(SubscriptionDto dto){
    SubscriptionEntity entity= new SubscriptionEntity();
    entity.setId(dto.getId());
    entity.setUser_login_id(dto.getUser_login_id());
    entity.setChannel_name(dto.getChannel_name());
    entity.setCreatedAt(dto.getCreatedAt());
    entity.setNotification(dto.isNotification());
    return entity;
  }

  public SubscriptionDto toDto(SubscriptionEntity entity){
    SubscriptionDto dto= new SubscriptionDto();
    dto.setId(entity.getId());
    dto.setUser_login_id(entity.getUser_login_id());
    dto.setChannel_name(entity.getChannel_name());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setNotification(entity.isNotification());
    return dto;
  }
}
