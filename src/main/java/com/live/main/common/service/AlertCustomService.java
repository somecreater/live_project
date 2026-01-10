package com.live.main.common.service;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.entity.AlertEventEntity;
import com.live.main.common.database.mapper.AlertMapper;
import com.live.main.common.database.repository.AlertRepository;
import com.live.main.common.service.Interface.AlertCustomServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlertCustomService implements AlertCustomServiceInterface {

  private final AlertMapper mapper;
  private final AlertRepository alertRepository;

  @Override
  @Transactional
  public Long save(String userId, AlertEvent alertEvent) {
      AlertEventEntity alertEventEntity= mapper.toEntity(alertEvent, userId);
      AlertEventEntity event=alertRepository.save(alertEventEntity);
      return event.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public List<AlertEvent> get(String userId) {
      List<AlertEventEntity> alertEventEntities = alertRepository.findByTargetUser(userId);
      if (alertEventEntities.isEmpty()) {
          return null;
      } else {
          return alertEventEntities.stream().map(mapper::toDto).toList();
      }
  }

  @Override
  @Transactional
  public void delete(String userId) {
    alertRepository.deleteByTargetUser(userId);
  }

  @Override
  @Transactional
  public void deleteById(String userId, Long id){
    AlertEventEntity entity=alertRepository.findById(id).orElse(null);
    if(entity ==null){
      return;
    }
    if(!Objects.equals(entity.getTargetUser(), userId)){
      return;
    }

    alertRepository.deleteById(id);
  }

  @Override
  @Transactional
  public boolean read(String userId, Long id){
    AlertEventEntity entity=alertRepository.findById(id).orElse(null);
    if(entity ==null){
      return false;
    }
    if(!Objects.equals(entity.getTargetUser(), userId)){
      return false;
    }

    int update=alertRepository.updateReadById(true,id);
    return update == 1;
  }

  @Override
  @Transactional
  public boolean readAll(String userId){
    int update=alertRepository.updateReadByTargetUser(true,userId);
    return update>0;
  }
}
