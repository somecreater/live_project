package com.live.main.channel.controller;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.SearchRequest;
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

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {

  private final ChannelServiceInterface channelService;

  @GetMapping("/info/{channel_id}")
  public ResponseEntity<?> getChannelInfo(@PathVariable Long channel_id){
    Map<String,Object> result=new HashMap<>();
    ChannelDto channelDto= channelService.getChannelInfo(channel_id);
    if(channelDto != null){
      result.put("result",true);
      result.put("channel",channelDto);
    }else{
      result.put("result",false);
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/search")
  public ResponseEntity<?> getChannelPage(@RequestBody SearchRequest request){
    Map<String,Object> result=new HashMap<>();
    Page<ChannelDto> channelDtoPage= channelService.getChannelList(
      request.getPage(), request.getSize(), request.getType(), request.getKeyword());
    if(channelDtoPage != null){
      result.put("result",true);
      result.put("channelPage", channelDtoPage);
    }else{
      result.put("result",false);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createChannel(@AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ChannelDto channelDto){
    Map<String,Object> result=new HashMap<>();
    if(principal.getUserid().compareTo(channelDto.getUser_login_id()) != 0 ){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    ChannelDto newChannel=channelService.createChannel(channelDto);

    result.put("result",true);
    result.put("new_channel",newChannel);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateChannel(@AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ChannelDto channelDto){
    Map<String,Object> result=new HashMap<>();
    if(principal.getUserid().compareTo(channelDto.getUser_login_id()) != 0 ){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    ChannelDto updateChannel= channelService.updateChannel(channelDto);

    result.put("result",true);
    result.put("update_channel",updateChannel);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteChannel(@AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ChannelDto channelDto){
    Map<String,Object> result=new HashMap<>();
    if(principal.getUserid().compareTo(channelDto.getUser_login_id()) != 0 ){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(channelService.deleteChannel(channelDto)){
      result.put("result",true);
    }
    return ResponseEntity.ok(result);
  }
}
