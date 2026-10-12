package com.live.main.video.service;

import com.live.main.common.database.dto.VideoValidationEvent;
import com.live.main.video.service.Interface.VideoMediaServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAdapter {


    private final KafkaTemplate<String, VideoValidationEvent> kafkaTemplate;
    private final VideoMediaServiceInterface videoMediaService;


}
