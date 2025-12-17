package com.live.main.channel.controller;

import com.live.main.channel.database.dto.PostDeleteRequest;
import com.live.main.channel.database.dto.PostDto;
import com.live.main.channel.database.dto.PostSearchRequest;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.PostServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostServiceInterface postService;
  private final ChannelServiceInterface channelService;

  @GetMapping("/read/{post_id}")
  public ResponseEntity<?> readPost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @PathVariable Long post_id
  ){
    Map<String, Object> result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/list")
  public ResponseEntity<?> searchPost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostSearchRequest postSearchRequest
  ){
    Map<String, Object> result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/write")
  public ResponseEntity<?> writePost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostDto postDto
  )
  {
    Map<String, Object> result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> updatePost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostDto postDto
  ){
    Map<String, Object> result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deletePost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostDeleteRequest postDeleteRequest
  ){
    Map<String, Object> result= new HashMap<>();

    return ResponseEntity.ok(result);
  }

}
