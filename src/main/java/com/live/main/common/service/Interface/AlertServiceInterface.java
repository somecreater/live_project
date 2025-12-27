package com.live.main.common.service.Interface;

import com.live.main.common.database.dto.AlertEvent;

/**알림 전송 서비스 (2025-12-27)*/
public interface AlertServiceInterface {
  /**알림 전송 기능*/
  public AlertEvent sendAlert(AlertEvent alertEvent);
}
