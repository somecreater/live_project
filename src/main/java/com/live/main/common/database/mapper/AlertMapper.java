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
                .alertType(alertEvent.getType())
                .publisher(alertEvent.getPublisher())
                .content(alertEvent.getContent())
                .targetUser(targetUser)
                .build();
    }

    public AlertEvent toDto(AlertEventEntity alert) {
      return new AlertEvent(
              alert.getAlertType(),
              alert.getPublisher(),
              alert.getContent()
      );
    }
}