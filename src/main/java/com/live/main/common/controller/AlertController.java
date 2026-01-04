package com.live.main.common.controller;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.service.Interface.AlertServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alert")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

  private final AlertServiceInterface alertService;

  @PostMapping("/get_alert")
  public ResponseEntity<?> getAlertList(Principal principal) {
    log.info("[POST] /api/alert/get_alert - {}", principal.getName());
    Map<String, Object> result = new HashMap<>();

    List<AlertEvent> alertEvents = alertService.sendAlertList(principal.getName());

    result.put("result", alertEvents != null);
    result.put("alerts", alertEvents);

    return ResponseEntity.ok(result);
  }
}
