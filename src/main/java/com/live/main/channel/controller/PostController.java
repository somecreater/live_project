package com.live.main.channel.controller;

import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.PostServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostServiceInterface postService;
  private final ChannelServiceInterface channelService;
}
