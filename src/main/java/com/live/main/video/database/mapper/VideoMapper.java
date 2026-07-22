package com.live.main.video.database.mapper;

import com.live.main.channel.database.repository.ChannelRepository;
import com.live.main.video.database.dto.VideoDto;
import com.live.main.video.database.entity.VideoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoMapper {
  private final ChannelRepository channelRepository;

  public VideoEntity toEntity(VideoDto videoDto){
    VideoEntity entity= new VideoEntity();
    entity.setId(videoDto.getId());
    entity.setTitle(videoDto.getTitle());
    entity.setDescription(videoDto.getDescription());
    entity.setFile_type(videoDto.getFile_type());
    entity.setSize(videoDto.getSize());
    entity.setAllow_comments(videoDto.isAllow_comments());
    entity.setVisibility(videoDto.getVisibility());
    entity.setProcessingStatus(videoDto.getProcessingStatus());
    entity.setStatus(videoDto.getStatus());
    entity.setDuration_seconds(videoDto.getDuration_seconds());
    entity.setLike(videoDto.getLike());
    entity.setUnlike(videoDto.getUnlike());
    entity.setView_count(videoDto.getView_count());
    entity.setThumbnail_url(videoDto.getThumbnail_url());
    entity.setOriginalObjectKey(videoDto.getOriginalObjectKey());
    entity.setHlsObjectKey(videoDto.getHlsObjectKey());
    entity.setExpectedFileSize(videoDto.getExpectedFileSize());
    if(videoDto.getChannel_id() != null){
      entity.setChannelEntity(channelRepository.findById(
        videoDto.getChannel_id()
      ).orElse(null));
    }else if(videoDto.getChannel_name() != null){
      entity.setChannelEntity(channelRepository.findByName(
              videoDto.getChannel_name()
      ).orElse(null));
    }
    entity.setCreatedAt(videoDto.getCreatedAt());
    entity.setUpdatedAt(videoDto.getUpdatedAt());
    return entity;
  }

  public VideoDto toDto(VideoEntity entity){
    VideoDto dto= new VideoDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setDescription(entity.getDescription());
    dto.setFile_type(entity.getFile_type());
    dto.setSize(entity.getSize());
    dto.setAllow_comments(entity.isAllow_comments());
    dto.setVisibility(entity.getVisibility());
    dto.setProcessingStatus(entity.getProcessingStatus());
    dto.setStatus(entity.getStatus());
    dto.setDuration_seconds(entity.getDuration_seconds());
    dto.setLike(entity.getLike());
    dto.setUnlike(entity.getUnlike());
    dto.setView_count(entity.getView_count());
    dto.setThumbnail_url(entity.getThumbnail_url());
    dto.setOriginalObjectKey(entity.getOriginalObjectKey());
    dto.setHlsObjectKey(entity.getHlsObjectKey());
    dto.setExpectedFileSize(entity.getExpectedFileSize());
    if(entity.getChannelEntity() != null){
      dto.setChannel_id(entity.getChannelEntity().getId());
      dto.setChannel_name(entity.getChannelEntity().getName());
    }
    dto.setCreatedAt(entity.getCreatedAt());
    dto.setUpdatedAt(entity.getUpdatedAt());
    return dto;
  }

}
