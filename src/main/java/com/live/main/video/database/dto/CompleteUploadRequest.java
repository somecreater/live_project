package com.live.main.video.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**최종 멀티파트 업로드 완료 후 조립 요청*/
@Data
@AllArgsConstructor
public class CompleteUploadRequest {
    private Long videoId;
    private String key;
    private String uploadId;
    private List<CompletePartRequest> parts;
}
