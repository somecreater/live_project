package com.live.main.channel.service.Interface;

import com.live.main.channel.database.dto.ChannelDto;
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
}
