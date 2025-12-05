package com.live.main.channel.service.Interface;

import com.live.main.channel.database.dto.ChannelDto;
import org.springframework.data.domain.Page;

/**채널 기능 (2025-12-04)*/
public interface ChannelServiceInterface {
  /**채널 정보 확인*/
  public ChannelDto getChannelInfo(Long id);
  /**채널 정보 확인(회원 아이디)*/
  public ChannelDto getChannelInfoUser(String LoginId);
  /**채널 목록(검색 포함) 확인*/
  public Page<ChannelDto> getChannelList(int page, int size, String type, String keyword);
  /**신규 채널 등록*/
  public ChannelDto createChannel(ChannelDto channelDto);
  /**채널 정보 수정*/
  public ChannelDto updateChannel(ChannelDto channelDto);
  /**채널 삭제*/
  public boolean deleteChannel(ChannelDto channelDto);
}
