package com.live.main.video.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**멀티파트 업로드에 대해서 프론트가 미리 서명된 URL 요청시 사용*/
@Data
@AllArgsConstructor
public class PresignPartsRequest {
    private Long videoId;
    private String key;
    private String uploadId;
    private List<Integer> partNumbers;
}
