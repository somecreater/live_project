package com.live.main.channel.database.dto;

import lombok.Getter;

@Getter
public class ChannelDeleteEvent {
  private final String name;

  public ChannelDeleteEvent(String name){
    this.name= name;
  }
}
