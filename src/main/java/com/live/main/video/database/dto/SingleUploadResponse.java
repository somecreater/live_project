package com.live.main.video.database.dto;

import java.time.LocalDateTime;

public record SingleUploadResponse(
        Long videoId,
        String uploadUrl,
        LocalDateTime expiresAt
) {
}
