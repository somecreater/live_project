package com.live.main.video.database.mapper;

import com.live.main.video.database.dto.UploadSessionDto;
import com.live.main.video.database.entity.UploadSessionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UploadSessionMapper {

  public UploadSessionEntity toEntity(UploadSessionDto dto){
      if (dto == null) {
          return null;
      }

      UploadSessionEntity entity = new UploadSessionEntity();
      entity.setId(dto.getId());
      entity.setUploadId(dto.getUploadId());
      entity.setVideoId(dto.getVideoId());
      entity.setObjectKey(dto.getObjectKey());
      entity.setStatus(dto.getStatus());
      entity.setTotalPartCount(dto.getTotalPartCount());
      entity.setCompletedPartCount(dto.getCompletedPartCount());
      entity.setPartSize(dto.getPartSize());
      entity.setCompletedAt(dto.getCompletedAt());
      entity.setAbortedAt(dto.getAbortedAt());
      entity.setCreatedAt(dto.getCreatedAt());
      entity.setUpdatedAt(dto.getUpdatedAt());

      return entity;
  }

  public UploadSessionDto toDto(UploadSessionEntity entity){
      if (entity == null) {
          return null;
      }

      UploadSessionDto dto = new UploadSessionDto();
      dto.setId(entity.getId());
      dto.setUploadId(entity.getUploadId());
      dto.setVideoId(entity.getVideoId());
      dto.setObjectKey(entity.getObjectKey());
      dto.setStatus(entity.getStatus());
      dto.setTotalPartCount(entity.getTotalPartCount());
      dto.setCompletedPartCount(entity.getCompletedPartCount());
      dto.setPartSize(entity.getPartSize());
      dto.setCompletedAt(entity.getCompletedAt());
      dto.setAbortedAt(entity.getAbortedAt());
      dto.setCreatedAt(entity.getCreatedAt());
      dto.setUpdatedAt(entity.getUpdatedAt());

      return dto;
  }
}
