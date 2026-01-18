package com.live.main.common.controller;

import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.common.database.dto.ManagerMessageRequest;
import com.live.main.common.service.Interface.AlertCustomServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/manager_message")
@RequiredArgsConstructor
@Slf4j
public class ManagerAlertController {

    private final AlertCustomServiceInterface alertCustomService;

    @PostMapping("/get_message")
    public ResponseEntity<?> getAdminMessage(Principal principal, @RequestBody ManagerMessageRequest request) {
        log.info("[POST] /api/manager_message/manager_message - userId: {}, page: {}, size: {}",
                principal.getName(), request.getPage(), request.getSize());
        Map<String, Object> result = new HashMap<>();
        Page<ManagerMessageEvent> managerMessageEvents = alertCustomService.getAdminMessage(
                principal.getName(),
                request.getPage(),
                request.getSize()
        );
        result.put("result", managerMessageEvents!=null);
        result.put("manager_messages", managerMessageEvents);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/read_message/{id}")
    public ResponseEntity<?> readAdminMessage(Principal principal, @PathVariable Long id){
        log.info("[POST] /api/manager_message/manager_message/{} - {}", id, principal.getName());
        Map<String, Object> result = new HashMap<>();
        boolean isRead=alertCustomService.readAdminMessage(principal.getName(), id);
        result.put("result", isRead);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/readAll_message")
    public ResponseEntity<?> readAllAdminMessage(Principal principal) {
        log.info("[POST] /api/manager_message/readAll_message - {}", principal.getName());
        Map<String, Object> result = new HashMap<>();
        boolean isRead = alertCustomService.readAllAdminMessage(principal.getName());
        result.put("result", isRead);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteAlert(Principal principal, @PathVariable Long id){
        log.info("[POST] /api/manager_message/delete/{} - {}", id, principal.getName());
        Map<String, Object> result = new HashMap<>();
        alertCustomService.deleteAdminMessageById(principal.getName(),id);
        result.put("result", true);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/deleteByUser")
    public ResponseEntity<?> deleteByUser(Principal principal) {
        log.info("[POST] /api/manager_message/deleteByUser - {}", principal.getName());
        Map<String, Object> result = new HashMap<>();
        alertCustomService.deleteAdminMessage(principal.getName());
        result.put("result", true);
        return ResponseEntity.ok(result);
    }
}
