package com.live.main.common.service;

import com.live.main.common.service.Interface.CommonServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommonService implements CommonServiceInterface {

  private final Tika tika;
  private final String[] safeType={"image/png","image/jpg","image/jpeg","image/gif"};
  @Override
  public String maskData(String data) {
    if (data.length() <= 2) return data.charAt(0) + "*";
    return data.substring(0, data.length() - 2) + "**";
  }

  @Override
  public boolean isSafeFile(MultipartFile file){
      try {
        InputStream in= file.getInputStream();
        String type=tika.detect(in);
        log.info("{} 파일 확장자: {}",file.getOriginalFilename(), type);
        return true;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
  }
}
