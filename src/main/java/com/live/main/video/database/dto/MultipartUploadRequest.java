package com.live.main.video.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**멀티파트 업로드를 위한 요청(API 서버에서 생성)*/
@Data
@AllArgsConstructor
public class MultipartUploadRequest {
    private Long videoId;
    private String key;
    private String uploadId;
    private Long partSize;
    private Integer totalPartCount;
}
