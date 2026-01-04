package com.live.main.common.config;

import com.live.main.user.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

  //나중에 API 호출로 변경(분리시 삭제)
  private final JwtService jwtService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel){

    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    log.info("[WS INTERCEPTOR] Command: {}, SessionId: {}",
            accessor.getCommand(),
            accessor.getSessionId());

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      log.info("[WS INTERCEPTOR] CONNECT command received");
      var sessionAttributes = accessor.getSessionAttributes();
      if (sessionAttributes != null) {
        Authentication principal = (Authentication) sessionAttributes.get("principal");

        if (principal != null) {
          accessor.setUser(principal);
          log.info("[WS INTERCEPTOR] ✅ Principal set from handshake - user: {}",
                  principal.getName());
        } else {
          log.warn("[WS INTERCEPTOR] ⚠️ No principal found in session attributes");
        }
      }
    }
    return message;
  }

}
