package com.live.main.video.controller;


import com.live.main.video.service.Interface.VideoServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

  private final VideoServiceInterface videoService;


}
