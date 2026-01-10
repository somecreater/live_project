package com.live.main.common.service.Interface;

import com.live.main.common.database.dto.AlertEvent;

import java.util.List;

/**알림 가져오기 기능(2026-01-09)*/
public interface AlertCustomServiceInterface {
  public Long save(String userId, AlertEvent alertEvent);
  public List<AlertEvent> get(String userId);
  public void delete(String userId);
  public void deleteById(String userId, Long id);
  public boolean read(String userId, Long id);
  public boolean readAll(String userId);
}
