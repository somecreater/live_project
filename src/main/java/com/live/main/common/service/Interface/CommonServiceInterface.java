package com.live.main.common.service.Interface;

import org.springframework.web.multipart.MultipartFile;

/**공통 기능을 포함한 서비스*/
public interface CommonServiceInterface { 
  /**데이터 마스킹*/
  public String maskData(String data);
  /**파일 검사*/
  public boolean isSafeFile(MultipartFile file);
}
