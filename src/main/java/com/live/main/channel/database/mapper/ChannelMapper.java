package com.live.main.channel.database.mapper;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.entity.ChannelEntity;
import com.live.main.user.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
  private final UserRepository userRepository;
  public ChannelEntity toEntity(ChannelDto channelDto){
    ChannelEntity entity= new ChannelEntity();
    entity.setId(channelDto.getId());
    entity.setName(channelDto.getName());
    entity.setDescription(channelDto.getDescription());
    entity.setUpdatedAt(channelDto.getUpdatedAt());
    entity.setCreatedAt(channelDto.getCreatedAt());
    if(channelDto.getUser_id() != null) {
        entity.setUsers(
          userRepository
            .findById(channelDto.getUser_id())
            .orElse(null));
    }else if(channelDto.getUser_login_id() != null){
        entity.setUsers(
          userRepository
            .findByLoginId(channelDto.getUser_login_id())
            .orElse(null)
        );
    }
    return entity;
  }

  public ChannelDto toDto(ChannelEntity channelEntity){
    ChannelDto channelDto=new ChannelDto();
    channelDto.setId(channelEntity.getId());
    channelDto.setName(channelEntity.getName());
    channelDto.setDescription(channelEntity.getDescription());
    channelDto.setUpdatedAt(channelEntity.getUpdatedAt());
    channelDto.setCreatedAt(channelEntity.getCreatedAt());
    if(channelEntity.getUsers() != null) {
      channelDto.setUser_id(channelEntity.getUsers().getId());
      channelDto.setUser_login_id(channelEntity.getUsers().getLoginId());
    }
    return channelDto;
  }
}
