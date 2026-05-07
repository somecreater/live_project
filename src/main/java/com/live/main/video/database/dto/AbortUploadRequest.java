package com.live.main.video.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**멀티 파트 업로드 취소 or 실패시 보낼 요청*/
@Data
@AllArgsConstructor
public class AbortUploadRequest {
    private Long videoId;
    private String key;
    private String uploadId;
}
