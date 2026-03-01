package com.live.main.common.service;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.common.database.entity.AlertEventEntity;
import com.live.main.common.database.entity.ManagerMessageEntity;
import com.live.main.common.database.mapper.AlertMapper;
import com.live.main.common.database.mapper.ManagerMessageMapper;
import com.live.main.common.database.repository.AlertRepository;
import com.live.main.common.database.repository.ManagerMessageRepository;
import com.live.main.common.service.Interface.AlertCustomServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlertCustomService implements AlertCustomServiceInterface {

  private final AlertMapper mapper;
  private final AlertRepository alertRepository;

  private final ManagerMessageMapper managerMessageMapper;
  private final ManagerMessageRepository managerMessageRepository;

  @Override
  @Transactional
  public Long save(String userId, AlertEvent alertEvent) {
      AlertEventEntity alertEventEntity= mapper.toEntity(alertEvent, userId);
      alertEventEntity.setCreatedAt(alertEvent.getCreatedAt());
      AlertEventEntity event=alertRepository.save(alertEventEntity);
      return event.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AlertEvent> get(String userId, int page, int size) {
      PageRequest pageRequest=PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"createdAt"));
      Page<AlertEventEntity> alertEventEntities = alertRepository.findByTargetUser(userId, pageRequest);
      if (alertEventEntities.getTotalElements() == 0) {
          return null;
      } else {
          return alertEventEntities.map(mapper::toDto);
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

    int update=alertRepository.updateReadById(id);
    return update == 1;
  }

  @Override
  @Transactional
  public boolean readAll(String userId){
    int update=alertRepository.updateReadByTargetUser(userId);
    return update>0;
  }

  @Override
  @Transactional
  public Long saveAdminMessage(ManagerMessageEvent managerMessageEvent) {
    ManagerMessageEntity entity=managerMessageMapper.toEntity(managerMessageEvent);
    ManagerMessageEntity saved=managerMessageRepository.save(entity);
    return saved.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ManagerMessageEvent> getAdminMessage(String userId, int page, int size) {
    PageRequest pageRequest=PageRequest.of(page,size);
    Page<ManagerMessageEntity> entities=managerMessageRepository.
      findByTargetIdOrderByCreatedAtDesc(userId,pageRequest);
    if(!entities.isEmpty()){
      return entities.map(managerMessageMapper::toDto);
    }
    return null;
  }

  @Override
  @Transactional
  public void deleteAdminMessage(String userId) {
    managerMessageRepository.deleteByTargetId(userId);
  }

  @Override
  @Transactional
  public void deleteAdminMessageById(String userId, Long id) {
    ManagerMessageEntity entity=managerMessageRepository.findById(id).orElse(null);
    if(entity ==null){
      return;
    }
    if(!Objects.equals(entity.getTargetId(), userId)){
      return;
    }

    managerMessageRepository.deleteById(id);
  }

  @Override
  public boolean readAdminMessage(String userId, Long id) {
    ManagerMessageEntity entity=managerMessageRepository.findById(id).orElse(null);
    if(entity ==null){
      return false;
    }
    if(!Objects.equals(entity.getTargetId(), userId)){
      return false;
    }

    int update =managerMessageRepository.updateReadById(id);
    return update != 0;
  }

  @Override
  public boolean readAllAdminMessage(String userId) {
    int update=managerMessageRepository.updateReadByTargetId(userId);
    return update>0;
  }
}
