package com.live.main.common.service;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.service.Interface.AlertServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlertService implements AlertServiceInterface {
  private final SimpMessagingTemplate messagingTemplate;

    @Override
    @EventListener
    public AlertEvent sendAlert(AlertEvent alertEvent) {
      SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
      headerAccessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
      headerAccessor.setNativeHeader("eventType", alertEvent.getType().getType());
      headerAccessor.setNativeHeader("priority", alertEvent.getType().getPriority());
      headerAccessor.setLeaveMutable(true);
      MessageHeaders headers = headerAccessor.getMessageHeaders();

      messagingTemplate.convertAndSend("/topic/alerts/"+alertEvent.getReceiver(),
        alertEvent.getContent(),
        headers);
      return null;
    }
}
