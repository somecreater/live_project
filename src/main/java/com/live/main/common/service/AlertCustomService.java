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

@Service
@RequiredArgsConstructor
public class AlertCustomService implements AlertCustomServiceInterface {

  private final AlertMapper mapper;
  private final AlertRepository alertRepository;

  @Override
  @Transactional
  public void save(String userId, AlertEvent alertEvent) {
      AlertEventEntity alertEventEntity= mapper.toEntity(alertEvent, userId);
      alertRepository.save(alertEventEntity);
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
}
