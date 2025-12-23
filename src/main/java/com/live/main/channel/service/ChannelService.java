package com.live.main.channel.service;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.SubscriptionDto;
import com.live.main.channel.database.entity.ChannelEntity;
import com.live.main.channel.database.entity.SubscriptionEntity;
import com.live.main.channel.database.mapper.ChannelMapper;
import com.live.main.channel.database.mapper.SubscriptionMapper;
import com.live.main.channel.database.repository.ChannelRepository;
import com.live.main.channel.database.repository.SubscriptionRepository;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService implements ChannelServiceInterface {

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMapper subscriptionMapper;
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
  public ChannelDto getChannelInfoByName(String name){
    ChannelEntity channelEntity= channelRepository.findByName(name).orElse(null);
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
    if (channelEntityPage.hasContent()) {
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
    entity.setSubscription_count(0L);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    ChannelEntity newChannel = channelRepository.save(entity);
    if(newChannel.getId() == null){
      return null;
    }else{
      return channelMapper.toDto(newChannel);
    }
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


  @Override
  @Transactional
  public boolean deleteChannelOnUser(String userLoginId){
    try {
      ChannelEntity entity = channelRepository.findByUsers_LoginId(userLoginId).orElse(null);

      if (entity == null) return true;
      channelRepository.delete(entity);
      return true;
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SubscriptionDto> getSubscriptionPageByUser(
    int page, int size, String keyword, String userLoginId
  ){
    Pageable pageable = PageRequest.of(page, size);
    Page<SubscriptionEntity> subscriptionEntityPage= null;
    if(userLoginId.isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(keyword.isBlank()){
      subscriptionEntityPage= subscriptionRepository.findByUserLoginId(userLoginId,pageable);
    }else{
      subscriptionEntityPage=subscriptionRepository.findByUserLoginIdAndChannelNameContaining(userLoginId, keyword, pageable);
    }

    if(subscriptionEntityPage.hasContent()){
      return subscriptionEntityPage.map(subscriptionMapper::toDto);
    }else{
      return null;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SubscriptionDto> getSubscriptionPageByChannel(
    int page, int size, String keyword, String channel_name
  ){
    Pageable pageable = PageRequest.of(page, size);
    Page<SubscriptionEntity> subscriptionEntityPage= null;
    if(channel_name.isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(keyword.isBlank()){
      subscriptionEntityPage= subscriptionRepository.findByChannelName(channel_name,pageable);
    }else{
      subscriptionEntityPage= subscriptionRepository.findByChannelNameAndUserLoginIdLike(channel_name, keyword, pageable);
    }

    if(subscriptionEntityPage.hasContent()){
      return subscriptionEntityPage.map(subscriptionMapper::toDto);
    }else{
      return null;
    }
  }

  @Override
  @Transactional
  public SubscriptionDto insertSubscription(SubscriptionDto subscriptionDto){
    ChannelEntity channelEntity= channelRepository.findByName(subscriptionDto.getChannelName()).orElse(null);
    boolean is_subscription= subscriptionRepository.existsByUserLoginIdAndChannelName(
      subscriptionDto.getUserLoginId(), subscriptionDto.getChannelName()
    );
    if(channelEntity == null || is_subscription){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    SubscriptionEntity subscriptionEntity= subscriptionMapper.toEntity(subscriptionDto);
    subscriptionEntity.setCreatedAt(LocalDateTime.now());

    SubscriptionEntity newSubscription= subscriptionRepository.save(subscriptionEntity);
    if(newSubscription.getId() == null){
      return null;
    }else{
      return subscriptionMapper.toDto(newSubscription);
    }
  }

  public boolean checkSubscription(String user_login_id, String channel_name){
    return subscriptionRepository.existsByUserLoginIdAndChannelName(user_login_id,channel_name);
  }
  @Override
  @Transactional
  public SubscriptionDto updateSubscription(SubscriptionDto subscriptionDto){
    SubscriptionEntity subscriptionEntity= subscriptionRepository.findById(subscriptionDto.getId()).orElse(null);
    if(subscriptionEntity == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    subscriptionEntity.setNotification(subscriptionDto.isNotification());
    SubscriptionEntity update= subscriptionRepository.save(subscriptionEntity);
    return subscriptionMapper.toDto(update);
  }

  @Override
  @Transactional
  public boolean deleteSubscription(SubscriptionDto subscriptionDto){
    try {
      if (subscriptionDto.getUserLoginId().isBlank()
           || subscriptionDto.getChannelName().isBlank()) {
        throw new CustomException(ErrorCode.BAD_REQUEST);
      }

      subscriptionRepository.deleteByUserLoginIdAndChannelName(
        subscriptionDto.getUserLoginId(), subscriptionDto.getChannelName()
      );

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  @Override
  @Transactional
  public boolean deleteSubscriptionOnUser(String user_login_id){
    try{
      subscriptionRepository.deleteByUserLoginId(user_login_id);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  @Transactional
  public boolean deleteSubscriptionOnChannel(String channel_name){
    try{
      subscriptionRepository.deleteByChannelName(channel_name);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
