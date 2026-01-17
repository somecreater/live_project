package com.live.main.common.service.Interface;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ManagerMessageEvent;
import org.springframework.data.domain.Page;

import java.util.List;

/**알림 가져오기 기능(2026-01-09)*/
public interface AlertCustomServiceInterface {
  /**알림 저장 기능*/
  public Long save(String userId, AlertEvent alertEvent);
  /**알림 가져오기 기능*/
  public List<AlertEvent> get(String userId);
  /**알림 삭제 기능*/
  public void delete(String userId);
  /**알림 개별 삭제 기능*/
  public void deleteById(String userId, Long id);
  /**알림 읽음 처리 기능*/
  public boolean read(String userId, Long id);
  /**모든 알림 읽음 처리 기능*/
  public boolean readAll(String userId);

  //관리자 메시지 관련 메소드

  /**관리자 메시지 저장 기능*/
  public Long saveAdminMessage(ManagerMessageEvent managerMessageEvent);
  /**관리자 메시지 가져오기 기능*/
  public Page<ManagerMessageEvent> getAdminMessage(String userId, int page, int size);
  /**관리자 메시지 삭제 기능*/
  public void deleteAdminMessage(String userId);
  /**관리자 메시지 개별 삭제 기능*/
  public void deleteAdminMessageById(String userId, Long id);
  /**관리자 메시지 읽음 처리 기능*/
  public boolean readAdminMessage(String userId, Long id);
  /**모든 관리자 메시지 읽음 처리 기능*/
  public boolean readAllAdminMessage(String userId);
}
