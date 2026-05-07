package com.live.main.video.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**각 Part에 대한 미리 서명된 업로드 URL*/
@Data
@AllArgsConstructor
public class PartPresignedUrlResponse {
    private Integer partNumber;
    private String url;
}
