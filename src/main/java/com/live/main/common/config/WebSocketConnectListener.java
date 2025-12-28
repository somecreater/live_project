package com.live.main.common.config;

import com.live.main.common.database.repository.OnlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketConnectListener {
  private final OnlineRepository onlineRepository;

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {

    StompHeaderAccessor accessor =
            StompHeaderAccessor.wrap(event.getMessage());

    if (accessor.getUser() == null) return;

    String memberId = accessor.getUser().getName();
    onlineRepository.delete(memberId);

    log.info("[WS SESSION DISCONNECT] memberId={}", memberId);
  }

}
