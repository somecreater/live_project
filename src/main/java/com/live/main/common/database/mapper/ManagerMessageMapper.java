package com.live.main.common.database.mapper;

import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.common.database.entity.AlertEventEntity;
import com.live.main.common.database.entity.ManagerMessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerMessageMapper {

  public ManagerMessageEntity toEntity(ManagerMessageEvent managerMessageEvent) {
    ManagerMessageEntity entity = new ManagerMessageEntity();
    entity.setId(managerMessageEvent.getId());
    entity.setTitle(managerMessageEvent.getTitle());
    entity.setContent(managerMessageEvent.getContent());
    entity.setPublisher(managerMessageEvent.getPublisher());
    entity.setTargetId(managerMessageEvent.getTargetId());
    entity.setRead(managerMessageEvent.isRead());
    entity.setCreatedAt(managerMessageEvent.getCreatedAt());
    entity.setUpdatedAt(managerMessageEvent.getCreatedAt());
    return entity;
  }

  public ManagerMessageEvent toDto(ManagerMessageEntity managerMessageEntity) {
    return new ManagerMessageEvent(
            managerMessageEntity.getId(),
            managerMessageEntity.getTitle(),
            managerMessageEntity.getContent(),
            managerMessageEntity.getPublisher(),
            managerMessageEntity.getTargetId(),
            managerMessageEntity.isRead(),
            managerMessageEntity.getCreatedAt()
    );
  }
}
