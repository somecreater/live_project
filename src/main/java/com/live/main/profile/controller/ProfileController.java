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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profile_image")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileServiceInterface profileService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> UploadProfileImage(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestParam("file") MultipartFile file){
    log.info("[POST] /api/profile_image/upload - {}", principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    ProfileImageDto profileImageDto= profileService.profile_upload(
      file, principal.getUserid());
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
  public ResponseEntity<?> GetProfileInfo(@RequestParam("user_id") String userLoginId){
    log.info("[GET] /api/profile_image/get_info - {}", userLoginId);
    Map<String,Object> result=new HashMap<>();

    ProfileImageDto dto=profileService.profile_get(userLoginId);
    result.put("result",true);
    result.put("image_info",dto);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/download")
  public ResponseEntity<?> DownloadProfileImage(@RequestParam("user_id") String userLoginId){
    log.info("[GET] /api/profile_image/download - {}", userLoginId);

    InputStreamResource resource=profileService.profile_download(userLoginId);
    String contentDisposition= "attachment; filename=\"" + resource.getFilename() + "\"";

    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
      .contentType(MediaType.APPLICATION_OCTET_STREAM)
      .body(resource);
  }

  @GetMapping("/get_image")
  public ResponseEntity<?> ReadProfileImage(@RequestParam("user_id") String userLoginId){
    log.info("[GET] /api/profile_image/get_image - {}", userLoginId);
    Map<String,Object> result=new HashMap<>();

    String profile_url=profileService.profile_read(userLoginId);

    result.put("result",true);
    result.put("image_url",profile_url);

    return ResponseEntity.ok(result);
  }
}
