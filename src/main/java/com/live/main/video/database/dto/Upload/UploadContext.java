package com.live.main.video.database.dto.Upload;

import com.live.main.video.database.entity.UploadSessionStatus;

public record UploadContext(
        Long videoId,
        String uploadId,
        String objectKey,
        int totalPartCount,
        long expectedFileSize,
        UploadSessionStatus status
) {
}
