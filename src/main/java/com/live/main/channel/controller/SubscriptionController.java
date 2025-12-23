package com.live.main.channel.controller;


import com.live.main.channel.database.dto.SubscriptionDto;
import com.live.main.channel.database.dto.SubscriptionRequest;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
  private final ChannelServiceInterface channelService;

  @GetMapping("/is_subscription")
  public ResponseEntity<?> subscriptionCheck(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestParam("channel_name") String channel_name){
    log.info("[GET] /api/subscription/is_subscription - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    boolean check= channelService.checkSubscription(principal.getUserid(), channel_name);
    if(check){
      result.put("result",true);
      result.put("subscription", true);
    }else{
      result.put("result",true);
      result.put("subscription", false);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/user")
  public ResponseEntity<?> userSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionRequest request){
    log.info("[POST] /api/subscription/user - {}", request.getName());
    Map<String,Object> result=new HashMap<>();
    Page<SubscriptionDto> subscriptionDtoPage= channelService.getSubscriptionPageByUser(
      request.getPage(),request.getSize(), request.getKeyword(), request.getName());
    if(subscriptionDtoPage == null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("subscription", subscriptionDtoPage);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/my_subscription")
  public ResponseEntity<?> mySubscriptionList(@AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionRequest request){
    log.info("[POST] /api/subscription/my_subscription - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(Objects.equals(principal.getUserid(), request.getName()) || request.getKeyword() != null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    Page<SubscriptionDto> subscriptionDtoPage= channelService.getSubscriptionPageByUser(
            request.getPage(),request.getSize(), request.getKeyword(), request.getName());
    if(subscriptionDtoPage == null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("subscription", subscriptionDtoPage);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/channel")
  public ResponseEntity<?> channelSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionRequest request){
    log.info("[POST] /api/subscription/channel - {}", request.getName());
    Map<String,Object> result=new HashMap<>();
    Page<SubscriptionDto> subscriptionDtoPage= channelService.getSubscriptionPageByChannel(
      request.getPage(),request.getSize(),request.getKeyword(), request.getName());
    if(subscriptionDtoPage == null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("subscription", subscriptionDtoPage);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/my_channel")
  public ResponseEntity<?> myChannelSubscriptionList(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionRequest request){
    log.info("[POST] /api/subscription/my_channel - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    String channel_name= channelService.getChannelInfoUser(principal.getUserid()).getName();
    if(!Objects.equals(request.getName(), channel_name) || request.getKeyword() != null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    Page<SubscriptionDto> subscriptionDtoPage= channelService.getSubscriptionPageByChannel(
      request.getPage(), request.getSize(), request.getKeyword(), request.getName());
    if(subscriptionDtoPage == null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("subscription", subscriptionDtoPage);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/insert")
  public ResponseEntity<?> subscriptionInsert(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    log.info("[POST] /api/subscription/insert - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(subscriptionDto == null || !Objects.equals(subscriptionDto.getUserLoginId(), principal.getUserid())){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    SubscriptionDto newSubscription= channelService.insertSubscription(subscriptionDto);
    if(newSubscription==null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("new_subscription", newSubscription);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> subscriptionUpdate(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    log.info("[POST] /api/subscription/update - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(subscriptionDto == null || !Objects.equals(subscriptionDto.getUserLoginId(), principal.getUserid())){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    SubscriptionDto updateSubscription= channelService.updateSubscription(subscriptionDto);
    result.put("result", true);
    result.put("update_subscription", updateSubscription);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> subscriptionDelete(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody SubscriptionDto subscriptionDto){
    log.info("[POST] /api/subscription/delete - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(subscriptionDto == null || !Objects.equals(subscriptionDto.getUserLoginId(), principal.getUserid())){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(channelService.deleteSubscription(subscriptionDto)){
      result.put("result",true);
    }else{
      result.put("result",false);
    }
    return ResponseEntity.ok(result);
  }

}
