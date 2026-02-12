package com.live.main.user.controller;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.PostDto;
import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.dto.SearchRequest;
import com.live.main.user.service.Interface.ManagerServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Slf4j
public class ManagerController {

  private final ManagerServiceInterface managerService;

  @PostMapping("/user_list")
  public ResponseEntity<?> getUserList(
          @AuthenticationPrincipal CustomUserDetails principal,
          @RequestBody SearchRequest request) {
    log.info("[POST] /manager/user_list - {}", principal.getUsername());
    Map<String,Object> result=new HashMap<>();
    Page<UserDto> userList = managerService.GetUserList(request.getPage(), request.getSize(), request.getType(), request.getKeyword());

    if(userList == null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("user_list", userList);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/channel_list")
  public ResponseEntity<?> getChannelList(
          @AuthenticationPrincipal CustomUserDetails principal,
          @RequestBody SearchRequest request){
    log.info("[POST] /manager/channel_list - {}", principal.getUsername());
    Map<String,Object> result=new HashMap<>();
    Page<ChannelDto> channelList = managerService.GetChannelList(request.getPage(), request.getSize(), request.getType(), request.getKeyword());

    if(channelList == null){
      result.put("result", false);
    }else {
      result.put("result", true);
      result.put("channel_list", channelList);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/post_list")
  public ResponseEntity<?> getPostList(
          @AuthenticationPrincipal CustomUserDetails principal,
          @RequestBody SearchRequest request){
    log.info("[POST] /manager/post_list - {}", principal.getUsername());
    Map<String,Object> result=new HashMap<>();
    Page<PostDto> postList = managerService.GetPostList(request.getPage(), request.getSize(), request.getType(), request.getKeyword());

    if(postList == null){
      result.put("result", false);
    }else {
      result.put("result", true);
      result.put("post_list", postList);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/force_delete/{userId}")
  public ResponseEntity<?> forceDeleteUser(
          @AuthenticationPrincipal CustomUserDetails principal,
          @PathVariable Long userId) {
    log.info("[POST] /manager/force_delete/{} - {}", userId, principal.getUsername());
    Map<String,Object> result=new HashMap<>();
    managerService.ForceDeleteUser(userId);
    result.put("result", true);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/send_message")
  public ResponseEntity<?> sendMessage(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ManagerMessageEvent messageEvent){
    log.info("[POST] /manager/send_message - {}", principal.getUsername());
    Map<String,Object> result=new HashMap<>();
    managerService.SendManagerMessage(
      messageEvent.getTitle(), messageEvent.getContent(), principal.getUsername(), messageEvent.getTargetId());
    result.put("result", true);
    return ResponseEntity.ok(result);
  }
}
