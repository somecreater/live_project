package com.live.main.channel.controller;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.SearchRequest;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.CoverServiceInterface;
import com.live.main.channel.service.Interface.PostServiceInterface;
import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
  private final VideoServiceInterface videoService;
  private final CoverServiceInterface coverService;
  private final PostServiceInterface postService;

  private final ApplicationEventPublisher publisher;

  @GetMapping("/my_channel")
  public ResponseEntity<?> getMyChannel(@AuthenticationPrincipal CustomUserDetails principal){
    log.info("[GET] /api/channel/my_channel - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    ChannelDto channelDto=channelService.getChannelInfoUser(principal.getUserid());

    if(channelDto != null){
      result.put("result", true);
      result.put("my_channel", channelDto);
    }else{
      result.put("result", false);
    }

    return ResponseEntity.ok(result);
  }
  @GetMapping("/info/{channel_id}")
  public ResponseEntity<?> getChannelInfo(@PathVariable Long channel_id){
    log.info("[GET] /api/channel/info/{} ", channel_id);
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

  @PostMapping("/search")
  public ResponseEntity<?> getChannelPage(@RequestBody SearchRequest request){
    log.info("[GET] /api/channel/search - page:{} size:{} type:{} keyword:{}",
            request.getPage(), request.getSize(), request.getType(), request.getKeyword());
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
    log.info("[POST] /api/channel/create - {}", principal.getUserid());
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
    log.info("[POST] /api/channel/update - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(principal.getUserid().compareTo(channelDto.getUser_login_id()) != 0 ){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    ChannelDto updateChannel= channelService.updateChannel(channelDto);

    result.put("result",true);
    result.put("update_channel",updateChannel);
    publisher.publishEvent(new AlertEvent(
      null,
      "CHANNEL_UPDATE",
      channelDto.getName(), channelDto.getName()+" 채널이 업데이트 되었습니다.",
      false));
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteChannel(@AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ChannelDto channelDto){
    log.info("[POST] /api/channel/delete - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(principal.getUserid().compareTo(channelDto.getUser_login_id()) != 0 ){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    boolean video_delete= videoService.VideoDeleteOnChannel(channelDto.getName());
    boolean cover_delete= coverService.cover_delete_on_channel(channelDto.getName());
    boolean post_delete= postService.deletePostOnChannel(channelDto.getName());
    boolean subscription_delete= channelService.deleteSubscriptionOnChannel(channelDto.getName());
    boolean channel_delete= channelService.deleteChannel(channelDto);

    if(video_delete && cover_delete && post_delete && subscription_delete && channel_delete){
      result.put("result",true);
    }else{
      result.put("result",false);
    }
    return ResponseEntity.ok(result);
  }
}
