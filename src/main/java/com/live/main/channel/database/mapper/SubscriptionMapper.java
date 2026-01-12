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
    entity.setUserLoginId(dto.getUserLoginId());
    entity.setChannelName(dto.getChannelName());
    entity.setCreatedAt(dto.getCreatedAt());
    entity.setNotification(dto.isNotification());
    return entity;
  }

  public SubscriptionDto toDto(SubscriptionEntity entity){
    SubscriptionDto dto= new SubscriptionDto();
    dto.setId(entity.getId());
    dto.setUserLoginId(entity.getUserLoginId());
    dto.setChannelName(entity.getChannelName());
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setNotification(entity.isNotification());
    return dto;
  }
}
