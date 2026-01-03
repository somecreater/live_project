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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

  //나중에 API 호출로 변경(분리시 삭제)
  private final JwtService jwtService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel){

    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      String bearer = accessor.getFirstNativeHeader("Authorization");

      if (bearer == null || !bearer.startsWith("Bearer ")) {
        return null;
      }

      String token = bearer.substring(7);

      if(jwtService.ValidationToken(token)){
        String memberId= jwtService.getUserId(token);
        String auth= jwtService.getAuth(token);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, token, List.of(new SimpleGrantedAuthority(auth)));
        log.info("[WS] Websocket Token AUTHENTICATION OK memberId={}", memberId);
        accessor.setUser(authentication);
      }else {
        return null;
      }
    }

    return message;
  }

}
