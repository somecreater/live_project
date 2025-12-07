package com.live.main.channel.service;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.entity.ChannelEntity;
import com.live.main.channel.database.mapper.ChannelMapper;
import com.live.main.channel.database.repository.ChannelRepository;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.UserDeleteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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
  public ChannelDto getChannelInfoUser(String LoginId){
    ChannelEntity channelEntity= channelRepository.findByUsers_LoginId(LoginId).orElse(null);
    if(channelEntity == null){
      return null;
    }

    return channelMapper.toDto(channelEntity);
  }
  @Override
  @Transactional(readOnly = true)
  public Page<ChannelDto> getChannelList(int page, int size, String type, String keyword) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ChannelEntity> channelEntityPage = null;
    if (type.equals("name")) {
      channelEntityPage = channelRepository.findByNameLike(keyword,pageable);
    } else if (type.equals("description")) {
      channelEntityPage = channelRepository.findByDescriptionLike(keyword, pageable);
    }else if(type.isEmpty()){
      channelEntityPage =channelRepository.findAll(pageable);
    }
    if (channelEntityPage != null) {
      return channelEntityPage.map(channelMapper::toDto);
    }else{
      return null;
    }
  }

  @Override
  @Transactional
  public ChannelDto createChannel(ChannelDto channelDto) {
    if(channelDto.getName().isEmpty()
    || channelDto.getUser_login_id().isEmpty()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    boolean isExist= channelRepository.existsByNameOrUsers_LoginId(
      channelDto.getName(), channelDto.getUser_login_id()
    );
    if(isExist){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    ChannelEntity entity=channelMapper.toEntity(channelDto);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    ChannelEntity newChannel = channelRepository.save(entity);
    return channelMapper.toDto(newChannel);
  }

  @Override
  @Transactional
  public ChannelDto updateChannel(ChannelDto channelDto) {
    if(channelDto.getName().isEmpty()
    || channelDto.getUser_login_id().isEmpty()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    ChannelEntity entity= channelRepository.findByName(channelDto.getName()).orElse(null);
    if(entity == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }else if(entity.getUsers().getLoginId().compareTo(channelDto.getUser_login_id())!=0){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    entity.setName(channelDto.getName());
    entity.setDescription(channelDto.getDescription());
    entity.setUpdatedAt(LocalDateTime.now());
    ChannelEntity updateEntity= channelRepository.save(entity);
    return channelMapper.toDto(updateEntity);
  }

  @Override
  @Transactional
  public boolean deleteChannel(ChannelDto channelDto) {
    try {
        if (channelDto.getName().isEmpty()
                || channelDto.getUser_login_id().isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        ChannelEntity entity = channelRepository.findByName(channelDto.getName()).orElse(null);
        if (entity == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } else if (entity.getUsers().getLoginId().compareTo(channelDto.getUser_login_id()) != 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        channelRepository.deleteByName(channelDto.getName());
    } catch (Exception e) {
        e.printStackTrace();
        throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    return true;
  }

  @Async
  @EventListener
  @Override
  @Transactional
  public void deleteChannel(UserDeleteEvent event){
    ChannelEntity entity = channelRepository.findByUsers_LoginId(event.getUserLoginId()).orElse(null);
    if(entity == null) return;
    channelRepository.delete(entity);
  }
}
