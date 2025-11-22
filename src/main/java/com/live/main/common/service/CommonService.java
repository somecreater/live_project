package com.live.main.common.service;

import com.live.main.common.service.Interface.CommonServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommonService implements CommonServiceInterface {

  @Override
  public String maskData(String data) {
    if (data.length() <= 2) return data.charAt(0) + "*";
    return data.substring(0, data.length() - 2) + "**";
  }
}
