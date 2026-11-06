package com.live.main.video.service;

import com.live.main.video.service.Interface.VideoUploadServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledJob {

    private final VideoUploadServiceInterface videoUploadService;

    @Scheduled(cron = "${app.file.cleanup-cron}")
    public void run(){
        videoUploadService.cleanupExpiredUploads();
    }

}
