package com.live.main.user.service.Interface;

import com.live.main.user.database.dto.UserDto;

/**추후 여러 기능이 추가됨에 따라 수정될 수도 있음 2025-11-17 */
public interface UserServiceInterface {

  /**회원 등록 기능 */
  public UserDto RegisterUser(UserDto userDto);
  /**로그인 기능 */
  public UserDto LoginUser(String id, String pass);
  /**회원 정보 가져오기 기능- 로그인 아이디 이용*/
  public UserDto GetUserInfo(String userId);
  /**회원 정보 가져오기 기능- 이메일 이용 */
  public UserDto GetUserInfoByEmail(String email);
  /**회원 정보 수정 기능 */
  public UserDto UpdateUser(UserDto userDto);
  /**비밀번호 수정 기능 */
  public boolean UpdatePassword(String userId, String oldPass, String newPass);
  /**회원 탈퇴 기능 */
  public boolean DeleteUser(UserDto userDto);
}
