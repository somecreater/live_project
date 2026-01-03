package com.live.main.common.controller;

import com.live.main.common.database.repository.OnlineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SessionController {
  private final OnlineRepository onlineRepository;

  @MessageMapping("/ping")
  public void ping(Principal principal){

    if (principal == null) {
      log.warn("[WS PING] principal is null");
      return;
    }
    String memberId= principal.getName();
    onlineRepository.extend(memberId);

    log.info("[WS PING] memberId={}", memberId);
  }

}
