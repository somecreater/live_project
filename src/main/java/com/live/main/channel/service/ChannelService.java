package com.live.main.channel.service;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.entity.ChannelEntity;
import com.live.main.channel.database.mapper.ChannelMapper;
import com.live.main.channel.database.repository.ChannelRepository;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService implements ChannelServiceInterface {

  private final ChannelRepository channelRepository;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional(readOnly = true)
  public ChannelDto getChannelInfo(Long id) {
    ChannelEntity channelEntity= channelRepository.findById(id).orElse(null);
    if(channelEntity == null){
      return null;
    }
    return channelMapper.toDto(channelEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> getChannelList(int page, int size, String type, String keyword) {
    List<ChannelEntity> channelEntityList= new ArrayList<>();
    if(type.equals("name")){

    }else if(type.equals("description")){

    }
    return List.of();
  }

  @Override
  @Transactional
  public ChannelDto createChannel(ChannelDto channelDto) {
    return null;
  }

  @Override
  @Transactional
  public ChannelDto updateChannel(ChannelDto channelDto) {
    return null;
  }

  @Override
  @Transactional
  public boolean deleteChannel(ChannelDto channelDto) {
    return false;
  }
}
