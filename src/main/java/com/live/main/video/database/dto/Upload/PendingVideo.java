package com.live.main.video.database.dto.Upload;

public record PendingVideo(
        Long videoId,
        String objectKey,
        long expectedFileSize,
        String contentType
) {
}
