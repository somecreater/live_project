package com.live.main.video.database.dto;

public record VideoPlaybackResponse(
        Long videoId,
        String playbackUrl
) {
}
