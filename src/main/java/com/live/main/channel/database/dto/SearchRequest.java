package com.live.main.channel.database.dto;

import lombok.Data;

@Data
public class SearchRequest {
  private int page;
  private int size;
  private String type;
  private String keyword;
}
