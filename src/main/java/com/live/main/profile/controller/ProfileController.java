package com.live.main.profile.controller;

import com.live.main.profile.database.dto.ProfileDeleteRequest;
import com.live.main.profile.database.dto.ProfileUploadRequest;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/profile_image")
@RequiredArgsConstructor
public class ProfileController {

  @PostMapping("/upload")
  public ResponseEntity<?> UploadProfileImage(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ProfileUploadRequest profileUploadRequest){

    return ResponseEntity.ok().build();
  }

  @PostMapping("/delete")
  public ResponseEntity<?> DeleteProfileImage(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody ProfileDeleteRequest profileDeleteRequest){

    return ResponseEntity.ok().build();
  }

  @GetMapping("/get_info")
  public ResponseEntity<?> GetProfileInfo(@RequestParam String userLoginId){

    return ResponseEntity.ok().build();
  }

  @GetMapping("/download")
  public ResponseEntity<?> DownloadProfileImage(@RequestParam String userLoginId){

    return ResponseEntity.ok().build();
  }
}
