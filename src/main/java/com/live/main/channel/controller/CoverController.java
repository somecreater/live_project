package com.live.main.channel.controller;

import com.live.main.channel.service.Interface.CoverServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cover_image")
@RequiredArgsConstructor
public class CoverController {

  private final CoverServiceInterface coverService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> UploadCoverImage(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestParam("file") MultipartFile file){
    log.info("[POST] /api/cover_image/upload - user: {}", principal.getUserid());
    Map<String, Object>  result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> DeleteCoverImage(
        @AuthenticationPrincipal CustomUserDetails principal){
    log.info("[POST] /api/cover_image/delete - user: {}", principal.getUserid());
    Map<String, Object>  result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @GetMapping("/get_info")
  public ResponseEntity<?> GetCoverInfo(@RequestParam("channel_name") String channelName){
    log.info("[GET] /api/cover_image/get_info - channel: {}", channelName);
    Map<String, Object>  result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @GetMapping("/download")
  public ResponseEntity<?> DownloadCover(@RequestParam("channel_name") String channelName){
    log.info("[GET] /api/cover_image/download - channel: {}", channelName);

    Map<String, Object>  result= new HashMap<>();

    return ResponseEntity.ok(result);
  }
  @GetMapping("/get_image")
  public ResponseEntity<?> ReadCover(@RequestParam("channel_name") String channelName){
    log.info("[GET] /api/cover_image/get_image - channel: {}", channelName);
    Map<String, Object>  result= new HashMap<>();

    return ResponseEntity.ok(result);
  }
}
