package com.live.main.video.service;

import com.live.main.common.database.dto.VideoEncodingEvent;
import com.live.main.video.database.dto.VideoPlaybackResponse;
import com.live.main.video.service.Interface.VideoMediaServiceInterface;
import org.springframework.stereotype.Service;

@Service
public class VideoMediaService implements VideoMediaServiceInterface {
    @Override
    public void validateUploadedVideo(Long videoId, String channelName, String userLoginId) {

    }

    @Override
    public void completeEncoding(VideoEncodingEvent event) {

    }

    @Override
    public VideoPlaybackResponse getPlaybackUrl(Long videoId, String userLoginId) {
        return null;
    }

    @Override
    public boolean isValidMp4OrMov(byte[] bytes) {
        return false;
    }

    @Override
    public void validateValidationRequest(Long videoId, String channelName, String userLoginId) {

    }

    @Override
    public void validateEncodingEvent(VideoEncodingEvent event) {

    }

    @Override
    public void validateObjectSize(Long actualSize, long expectedSize) {

    }

    @Override
    public void failValidationAndDelete(ValidationContext context) {

    }

    @Override
    public void markVideoFailed(Long videoId) {

    }

    @Override
    public String normalizePrefix(String value) {
        return "";
    }
}
