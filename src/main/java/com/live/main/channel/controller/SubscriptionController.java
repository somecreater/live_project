package com.live.main.channel.controller;


import com.live.main.channel.database.dto.SubscriptionDto;
import com.live.main.channel.database.dto.SubscriptionRequest;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
  private final ChannelServiceInterface channelService;

  @PostMapping("/user")
  public ResponseEntity<?> userSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionRequest request){
    log.info("[POST] /api/subscription/user - {}", request.getName());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/my_subscription")
  public ResponseEntity<?> mySubscriptionList(@AuthenticationPrincipal CustomUserDetails principal){
    log.info("[POST] /api/subscription/my_subscription - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/channel")
  public ResponseEntity<?> channelSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionRequest request){
    log.info("[POST] /api/subscription/channel - {}", request.getName());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/my_channel")
  public ResponseEntity<?> myChannelSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal){
    log.info("[POST] /api/subscription/my_channel - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/insert")
  public ResponseEntity<?> subscriptionInsert(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    log.info("[POST] /api/subscription/insert - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> subscriptionUpdate(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    log.info("[POST] /api/subscription/update - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> subscriptionDelete(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    log.info("[POST] /api/subscription/delete - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

}
