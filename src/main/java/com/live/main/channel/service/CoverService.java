package com.live.main.channel.service;

import com.live.main.channel.database.dto.ChannelDeleteEvent;
import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.CoverDto;
import com.live.main.channel.database.repository.CoverRepository;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.CoverServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@Service
public class CoverService implements CoverServiceInterface {

  private final CoverRepository coverRepository;
  private final ChannelServiceInterface channelService;

  @Transactional
  @Override
  public CoverDto cover_upload(MultipartFile file, String channel_name) {
    return null;
  }

  @Transactional
  @Override
  public void cover_delete(String channel_name) {

  }

  @Transactional
  @Override
  public void cover_file_delete(String channel_name) {

  }

  @Async
  @EventListener
  @Transactional
  @Override
  public void cover_delete_on_event(ChannelDeleteEvent deleteEvent) {

  }

  @Transactional
  @Override
  public CoverDto cover_read_info(String channel_name) {
    return null;
  }

  @Transactional
  @Override
  public String cover_read(String channel_name) {
    return "";
  }

  @Transactional
  @Override
  public InputStreamResource cover_download(String channel_name) {
    return null;
  }
}
