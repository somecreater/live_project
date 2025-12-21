package com.live.main.channel.controller;


import com.live.main.channel.database.dto.SubscriptionDto;
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

  @GetMapping("/user")
  public ResponseEntity<?> userSubscriptionList(@AuthenticationPrincipal CustomUserDetails principal,
    @RequestParam("user_login_id") String login_id){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @GetMapping("/my_subscription")
  public ResponseEntity<?> mySubscriptionList(@AuthenticationPrincipal CustomUserDetails principal){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @GetMapping("/channel")
  public ResponseEntity<?> channelSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestParam("channel_name") String channel_name){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/insert")
  public ResponseEntity<?> subscriptionInsert(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> subscriptionUpdate(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> subscriptionDelete(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

}
