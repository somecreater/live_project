package com.live.main.profile.controller;

import com.live.main.profile.database.dto.ProfileImageDto;
import com.live.main.profile.database.dto.ProfileUploadRequest;
import com.live.main.profile.service.Interface.ProfileServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profile_image")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileServiceInterface profileService;

  @PostMapping("/upload")
  public ResponseEntity<?> UploadProfileImage(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ProfileUploadRequest profileUploadRequest){
    log.info("[POST] /api/profile_image/upload - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    ProfileImageDto profileImageDto= profileService.profile_upload(
      profileUploadRequest.getFile(), principal.getUserid());
    result.put("result",true);
    result.put("image",profileImageDto);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> DeleteProfileImage(
    @AuthenticationPrincipal CustomUserDetails principal){
    log.info("[POST] /api/profile_image/delete - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    profileService.profile_delete(principal.getUserid());
    result.put("result",true);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/get_info")
  public ResponseEntity<?> GetProfileInfo(@RequestParam String userLoginId){
    log.info("[GET] /api/profile_image/get_info - {}", userLoginId);
    Map<String,Object> result=new HashMap<>();

    ProfileImageDto dto=profileService.profile_get(userLoginId);
    result.put("result",true);
    result.put("image_info",dto);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/download")
  public ResponseEntity<?> DownloadProfileImage(@RequestParam String userLoginId){
    log.info("[GET] /api/profile_image/download - {}", userLoginId);

    InputStreamResource resource=profileService.profile_download(userLoginId);
    String contentDisposition= "attachment; filename=\"" + resource.getFilename() + "\"";

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .body(resource);
  }
}
