package com.live.main.video.database.dto.Upload;

import java.time.LocalDateTime;

public record SingleUploadResponse(
        Long videoId,
        String uploadUrl,
        LocalDateTime expiresAt
) {
}
