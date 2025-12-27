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
import java.util.List;


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
  @Transactional(readOnly = true)
  public List<String> getsubscriptionUserList(String channel_name){
    try {
      return subscriptionRepository.findByChannelName(channel_name)
              .stream().map(SubscriptionEntity::getUserLoginId).toList();
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }

  @Override
  @Transactional
  public ChannelDto createChannel(ChannelDto channelDto) {
    if(channelDto.getName() == null
    || channelDto.getUser_login_id() == null
    || channelDto.getName().isBlank()
    || channelDto.getUser_login_id().isBlank()){
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
    if(channelDto.getName() == null
      || channelDto.getUser_login_id() == null
      || channelDto.getName().isBlank()
      || channelDto.getUser_login_id().isBlank()){
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
        if (channelDto.getName() == null
          || channelDto.getUser_login_id() == null
          || channelDto.getName().isBlank()
          || channelDto.getUser_login_id().isBlank()) {
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
      throw new CustomException(ErrorCode.SERVER_ERROR);
    }
  }

  @Override
  @Transactional
  public boolean increaseSubscription(String channel_name){
    try{
      ChannelEntity entity= channelRepository.findByName(channel_name).orElse(null);
      if(entity!=null){
        Long current_count=entity.getSubscription_count();
        if(current_count==null){
          entity.setSubscription_count(1L);
        }else {
          entity.setSubscription_count(current_count + 1);
          channelRepository.save(entity);
        }
        return true;
      }else{
        return false;
      }
    }catch (Exception e){
      e.printStackTrace();
      throw new CustomException(ErrorCode.SERVER_ERROR);
    }
  }

  @Override
  @Transactional
  public boolean decreaseSubscription(String channel_name){
    try{
      ChannelEntity entity= channelRepository.findByName(channel_name).orElse(null);
      if(entity!=null){
        Long current_count=entity.getSubscription_count();
        if( current_count == null|| current_count == 0){
          return false;
        }
        entity.setSubscription_count(current_count-1);
        channelRepository.save(entity);
        return true;
      }else{
        return false;
      }
    }catch (Exception e){
      e.printStackTrace();
      throw new CustomException(ErrorCode.SERVER_ERROR);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SubscriptionDto> getSubscriptionPageByUser(
    int page, int size, String keyword, String userLoginId
  ){
    Pageable pageable = PageRequest.of(page, size);
    Page<SubscriptionEntity> subscriptionEntityPage= null;
    if(userLoginId == null ||userLoginId.isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(keyword != null && !keyword.isBlank()){
      subscriptionEntityPage=subscriptionRepository.findByUserLoginIdAndChannelNameContaining(userLoginId, keyword, pageable);
    }else{
      subscriptionEntityPage= subscriptionRepository.findByUserLoginId(userLoginId,pageable);
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
    if(channel_name==null || channel_name.isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(keyword != null && !keyword.isBlank()){
      subscriptionEntityPage= subscriptionRepository.findByChannelNameAndUserLoginIdLike(channel_name, keyword, pageable);
    }else{
      subscriptionEntityPage= subscriptionRepository.findByChannelName(channel_name,pageable);
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

  @Override
  @Transactional
  public SubscriptionDto checkSubscription(String user_login_id, String channel_name){
    SubscriptionEntity entity = subscriptionRepository.findByUserLoginIdAndChannelName(
      user_login_id,channel_name).orElse(null);
    if(entity == null){
      return null;
    } else{
      return subscriptionMapper.toDto(entity);
    }
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
      if (subscriptionDto.getUserLoginId() == null
           || subscriptionDto.getChannelName() == null
           || subscriptionDto.getUserLoginId().isBlank()
           || subscriptionDto.getChannelName().isBlank()) {
        throw new CustomException(ErrorCode.BAD_REQUEST);
      }

      subscriptionRepository.deleteByUserLoginIdAndChannelName(
        subscriptionDto.getUserLoginId(), subscriptionDto.getChannelName()
      );

    } catch (Exception e) {
      e.printStackTrace();
      throw new CustomException(ErrorCode.SERVER_ERROR);
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
      throw new CustomException(ErrorCode.SERVER_ERROR);
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
      throw new CustomException(ErrorCode.SERVER_ERROR);
    }
  }
}
