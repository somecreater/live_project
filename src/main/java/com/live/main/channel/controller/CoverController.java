package com.live.main.channel.controller;

import com.live.main.channel.database.dto.CoverDto;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.CoverServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
  private final ChannelServiceInterface channelService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> UploadCoverImage(
        @AuthenticationPrincipal CustomUserDetails principal,
        @RequestParam("file") MultipartFile file){
    log.info("[POST] /api/cover_image/upload - user: {}", principal.getUserid());
    Map<String, Object>  result= new HashMap<>();
    CoverDto coverDto= coverService.cover_upload(file,
      channelService.getChannelInfoUser(principal.getUserid()).getName());

    result.put("result", true);
    result.put("image", coverDto);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> DeleteCoverImage(
        @AuthenticationPrincipal CustomUserDetails principal){
    log.info("[POST] /api/cover_image/delete - user: {}", principal.getUserid());
    Map<String, Object>  result= new HashMap<>();
    coverService.cover_delete(
      channelService.getChannelInfoUser(principal.getUserid()).getName());

    result.put("result", true);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/get_info")
  public ResponseEntity<?> GetCoverInfo(@RequestParam("channel_name") String channelName){
    log.info("[GET] /api/cover_image/get_info - channel: {}", channelName);
    Map<String, Object>  result= new HashMap<>();
    CoverDto coverDto= coverService.cover_read_info(channelName);

    if(coverDto != null){
      result.put("result", true);
      result.put("image_info", coverDto);
    }else{
      result.put("result",false);
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/download")
  public ResponseEntity<?> DownloadCover(@RequestParam("channel_name") String channelName){
    log.info("[GET] /api/cover_image/download - channel: {}", channelName);
    InputStreamResource resource=coverService.cover_download(channelName);
    String contentDisposition= "attachment; filename=\"" + resource.getFilename() + "\"";

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .body(resource);
  }
  @GetMapping("/get_image")
  public ResponseEntity<?> ReadCover(@RequestParam("channel_name") String channelName){
    log.info("[GET] /api/cover_image/get_image - channel: {}", channelName);
    Map<String, Object>  result= new HashMap<>();
    String cover_url= coverService.cover_read(channelName);

    result.put("result",true);
    result.put("image_url",cover_url);
    return ResponseEntity.ok(result);
  }
}
