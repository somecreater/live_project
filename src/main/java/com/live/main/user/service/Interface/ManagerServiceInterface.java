package com.live.main.user.service.Interface;

import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.user.database.dto.UserDto;
import org.springframework.data.domain.Page;

/**관리자 기능 2026-01-13 */
public interface ManagerServiceInterface {

  /**회원 목록 가져오기*/
  public Page<UserDto> GetUserList(int page, int size, String type, String keyword);

  /**회원 강제 탈퇴 처리 */
  public void ForceDeleteUser(Long userId);

  /**신고된 컨텐츠 검토 및 처리 */
  public boolean ReviewReportedContent(Long contentId, String contentType, String action);

  /**관리자 메시지 전송*/
  public void SendManagerMessage(ManagerMessageEvent managerMessageEvent);
}
