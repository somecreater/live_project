package com.live.main.video.controller;


import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.video.database.dto.VideoDto;
import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

  private final VideoServiceInterface videoService;

  @PostMapping("/upload-url")
  public ResponseEntity<?> getVideoUploadUrl(
          @AuthenticationPrincipal CustomUserDetails principal,
          @RequestBody VideoDto videoDto) {
    Map<String ,Object> result = new HashMap<>();

    return ResponseEntity.ok(result);
  }

}
