package com.live.main.channel.database.mapper;

import com.live.main.channel.database.dto.CoverDto;
import com.live.main.channel.database.entity.CoverEntity;
import com.live.main.channel.database.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoverMapper {
  private final ChannelRepository channelRepository;

  public CoverEntity toEntity(CoverDto coverDto){
    CoverEntity entity= new CoverEntity();
    entity.setId(coverDto.getId());
    entity.setImage_name(coverDto.getImage_name());
    entity.setImage_url(coverDto.getImage_url());
    entity.setSize(coverDto.getSize());
    entity.setFile_type(coverDto.getFile_type());
    entity.setCreatedAt(coverDto.getCreatedAt());
    entity.setUpdatedAt(coverDto.getUpdatedAt());
    if(coverDto.getChannel_id() != null){
      entity.setChannel(channelRepository.findById(coverDto.getChannel_id()).orElse(null));
    }else if(coverDto.getChannel_name() != null){
      entity.setChannel(channelRepository.findByName(coverDto.getChannel_name()).orElse(null));
    }
    return entity;
  }

  public CoverDto toDto(CoverEntity coverEntity){
    CoverDto dto= new CoverDto();
    dto.setId(coverEntity.getId());
    dto.setImage_name(coverEntity.getImage_name());
    dto.setImage_url(coverEntity.getImage_url());
    dto.setSize(coverEntity.getSize());
    dto.setFile_type(coverEntity.getFile_type());
    dto.setCreatedAt(coverEntity.getCreatedAt());
    dto.setUpdatedAt(coverEntity.getUpdatedAt());
    if(coverEntity.getChannel() != null){
      dto.setChannel_id(coverEntity.getChannel().getId());
      dto.setChannel_name(coverEntity.getChannel().getName());
    }
    return dto;
  }
}
