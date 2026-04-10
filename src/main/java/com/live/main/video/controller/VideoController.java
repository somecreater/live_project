package com.live.main.video.controller;


import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.video.database.dto.VideoDto;
import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

  private final VideoServiceInterface videoService;
  private final ChannelServiceInterface channelService;

  @PostMapping("/upload-url")
  public ResponseEntity<?> getVideoUploadUrl(
          @AuthenticationPrincipal CustomUserDetails principal,
          @RequestBody VideoDto videoDto) {
    Map<String ,Object> result = new HashMap<>();

    ChannelDto channelDto = channelService.getChannelInfoUser(principal.getUserid());
    if(channelDto.getName() == null
            || channelDto.getName().isBlank()){
        result.put("result", false);
    }

    Map<String, Object> sub_result= videoService.VideoUploadUrl(channelDto.getName(), principal.getUserid(), videoDto);
    Object urlObj = sub_result.get("url");
    Object videoIdObj = sub_result.get("video_id");

    if(urlObj == null || videoIdObj == null){
        result.put("result", false);
    } else {
        result.put("result", true);
        result.put("uploadUrl", urlObj.toString());
        result.put("video_id", ((Number) videoIdObj).longValue());
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/multipart-upload-url")
  public ResponseEntity<?> getMultipartVideoUploadUrl(
          @AuthenticationPrincipal CustomUserDetails principal,
          @RequestBody VideoDto videoDto) {
    Map<String ,Object> result = new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/upload-complete")
  public ResponseEntity<?> uploadCompleted(
      @AuthenticationPrincipal CustomUserDetails principal,
      @RequestParam("video_id") Long video_id){
    Map<String ,Object> result = new HashMap<>();
    ChannelDto channelDto = channelService.getChannelInfoUser(principal.getUserid());
    if(videoService.videoValidation(channelDto.getName(), video_id)) {
      result.put("result", true);
    }
    return ResponseEntity.ok(result);
  }
}
