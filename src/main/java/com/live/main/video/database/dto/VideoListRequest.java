package com.live.main.video.database.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**동영상 목록 검색시 보낼 요청(전체 검색, 채널 내 검색)*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoListRequest {

  private String channelName;
  private String keyword;

  @Min(0)
  @Builder.Default
  private int page = 0;

  @Min(1)
  @Max(100)
  @Builder.Default
  private int size = 20;
}
