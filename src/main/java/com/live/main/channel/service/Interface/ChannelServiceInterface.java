package com.live.main.channel.service.Interface;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.SubscriptionDto;
import org.springframework.data.domain.Page;

/**채널 기능 (2025-12-04)*/
public interface ChannelServiceInterface {
  /**채널 정보 확인 기능*/
  public ChannelDto getChannelInfo(Long id);
  /**채널 정보 확인 기능(이름)*/
  public ChannelDto getChannelInfoByName(String name);
  /**채널 정보 확인(회원 아이디) 기능*/
  public ChannelDto getChannelInfoUser(String LoginId);
  /**채널 목록(검색 포함) 확인 기능*/
  public Page<ChannelDto> getChannelList(int page, int size, String type, String keyword);
  /**신규 채널 등록 기능*/
  public ChannelDto createChannel(ChannelDto channelDto);
  /**채널 정보 수정 기능*/
  public ChannelDto updateChannel(ChannelDto channelDto);
  /**채널 삭제 기능*/
  public boolean deleteChannel(ChannelDto channelDto);
  /**채널 삭제 기능(회원 탈퇴시 사용)*/
  public boolean deleteChannelOnUser(String userLoginId);

  /**회원 채널 구독 목록 가져오기 기능*/
  public Page<SubscriptionDto> getSubscriptionPageByUser(
    int page, int size, String keyword, String userLoginId
  );
  /**채널 구독 회원 목록 가져오기 기능*/
  public Page<SubscriptionDto> getSubscriptionPageByChannel(
    int page, int size, String keyword, String channel_name
  );
  /**채널 구독 기능*/
  public SubscriptionDto insertSubscription(SubscriptionDto subscriptionDto);
  /**채널 구독여부 확인 기능*/
  public boolean checkSubscription(String user_login_id, String channel_name);
  /**채널 구독 상태 업데이트 기능*/
  public SubscriptionDto updateSubscription(SubscriptionDto subscriptionDto);
  /**채널 구독 해제 기능*/
  public boolean deleteSubscription(SubscriptionDto subscriptionDto);
  /**채널 구독 정보 삭제(회원 탈퇴시 사용)*/
  public boolean deleteSubscriptionOnUser(String user_login_id);
  /**채널 구독 정보 삭제(채널 삭제시 사용)*/
  public boolean deleteSubscriptionOnChannel(String channel_name);
}
