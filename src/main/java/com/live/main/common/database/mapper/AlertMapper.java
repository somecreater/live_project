package com.live.main.common.database.mapper;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.entity.AlertEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertMapper {

    public AlertEventEntity toEntity(AlertEvent alertEvent, String targetUser) {
      return AlertEventEntity.builder()
                .id(alertEvent.getId())
                .alertType(alertEvent.getType())
                .publisher(alertEvent.getPublisher())
                .content(alertEvent.getContent())
                .targetUser(targetUser)
                .read(alertEvent.isRead())
                .build();
    }

    public AlertEvent toDto(AlertEventEntity alert) {
      return new AlertEvent(
              alert.getId(),
              alert.getAlertType(),
              alert.getPublisher(),
              alert.getContent(),
              alert.isRead(),
              alert.getCreatedAt()
      );
    }
}