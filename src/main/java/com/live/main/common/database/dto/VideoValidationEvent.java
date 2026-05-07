package com.live.main.common.database.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class VideoValidationEvent {
    private final Long video_id;
    private final String object_key;

    public VideoValidationEvent(
      @JsonProperty("video_id") Long video_id,
      @JsonProperty("object_id") String object_id
    ){
      this.video_id=video_id;
      this.object_key=object_id;
    }
}
