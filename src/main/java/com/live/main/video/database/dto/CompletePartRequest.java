package com.live.main.video.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**멀티파트 파트 업로드 완료 요창*/
@Data
@AllArgsConstructor
public class CompletePartRequest {
    private Integer partNumber;
    private String etag;
}
