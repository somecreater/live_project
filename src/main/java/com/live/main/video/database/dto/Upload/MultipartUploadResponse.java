package com.live.main.video.database.dto.Upload;

public record MultipartUploadResponse(
        Long videoId,
        String uploadId,
        long partSize,
        int totalPartCount
) {
}
