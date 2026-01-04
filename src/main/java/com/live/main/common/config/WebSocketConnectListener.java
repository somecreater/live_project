package com.live.main.common.config;

import com.live.main.common.database.repository.OnlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketConnectListener {
  private final OnlineRepository onlineRepository;

  @EventListener
  public void handleConnectListener(SessionConnectedEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Principal principal = accessor.getUser();
    if (principal != null) {
      String memberId = principal.getName();
      String sessionId = accessor.getSessionId();

      log.info("[WS CONNECTED] memberId={}, sessionId={}", memberId, sessionId);

      // Redis에 온라인 상태 저장
      onlineRepository.save(memberId, sessionId);
    }
  }

  @EventListener
  public void handleDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Principal principal = accessor.getUser();
    if (principal== null) return;

    String memberId = principal.getName();
    log.info("[WS SESSION DISCONNECT] memberId={}", memberId);

    // Redis에 있는 온라인 상태 삭제
    onlineRepository.delete(memberId);
  }

}
