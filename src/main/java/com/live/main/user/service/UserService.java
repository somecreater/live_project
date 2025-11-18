package com.live.main.user.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.entity.LoginType;
import com.live.main.user.database.entity.UsersEntity;
import com.live.main.user.database.mapper.UserMapper;
import com.live.main.user.database.repository.UserRepository;
import com.live.main.user.service.Interface.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**추후 여러 기능이 추가됨에 따라 수정될 수도 있음 2025-11-17 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**사용자 아이디로 사용자 인증 객체 생성 */
  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UsersEntity> optionalUsers= userRepository.findByLoginId(username);
    if(optionalUsers.isEmpty()){
      throw new CustomException(ErrorCode.FAILURE_LOGIN);
    }
    return new CustomUserDetails(optionalUsers.get());
  }

  @Transactional
  @Override
  public UserDto RegisterUser(UserDto userDto) {
    UsersEntity users = null;
    if( userDto == null
      || userDto.getUserType() == null
      || userDto.getEmail() == null
      || userDto.getLoginId() == null
      || userDto.getNickname() == null
      || userDto.getLoginType() == null
      || userDto.getPassword() == null){
      log.info("회원 등록을 시도하려고 했고 일부 정보가 누락되었습니다");
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    if(userRepository.existsByLoginIdOrNickname(
        userDto.getLoginId(),
        userDto.getNickname()
    )){
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    if(userRepository.findByEmail(userDto.getEmail()).orElse(null) != null){
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }

    userDto.setCreatedAt(LocalDateTime.now());
    userDto.setUpdatedAt(LocalDateTime.now());
    users=userMapper.toEntity(userDto);
    users.setPassword(passwordEncoder.encode(userDto.getPassword()));
    UsersEntity newUser = userRepository.save(users);

    UserDto newUserInfo= userMapper.toDto(newUser);
    newUserInfo.setPassword(null);
    return newUserInfo;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto LoginUser(String id, String pass) {
    UsersEntity loginUser=userRepository.findByLoginId(id).orElse(null);
    if(loginUser == null){
      log.info("{} 에 대한 정보가 없습니다.",id);
      throw new CustomException(ErrorCode.FAILURE_LOGIN);
    }
    if(!passwordEncoder.matches(pass, loginUser.getPassword())){
      log.info("{} 이 로그인을 시도하였고 비밀번호가 틀렸습니다", id);
      throw new CustomException(ErrorCode.FAILURE_LOGIN);
    }

    return userMapper.toDto(loginUser);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto GetUserInfo(String LoginId) {
    UsersEntity usersEntity = null;
    if(LoginId != null){
      usersEntity = userRepository.findByLoginId(LoginId).orElse(null);
    }

    if(usersEntity == null){
      log.info("{} 아이디에 대한 정보가 없습니다.",LoginId);
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
    return userMapper.toDto(usersEntity);
  }

  public UserDto GetUserInfoByEmail(String email){
    UsersEntity users=userRepository.findByEmail(email).orElse(null);
    if(users == null){
      return null;
    }
    return userMapper.toDto(users);
  }
  @Override
  public UserDto UpdateUser(UserDto userDto) {
    UsersEntity users = null;
    if( userDto == null
        || userDto.getUserType() == null
        || userDto.getEmail() == null
        || userDto.getLoginId() == null
        || userDto.getNickname() == null
        || userDto.getPhone() == null
        || userDto.getLoginType() == null
        || userDto.getPassword() == null){
      log.info("회원 정보 수정을 시도하려고 했고 일부 정보가 누락되었습니다");
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    if(userRepository.existsByEmailOrPhoneOrNickname(
        userDto.getEmail(),
        userDto.getPhone(),
        userDto.getNickname()
    )){
      log.info("변경 가능한 정보들이 모두 일치하거나 다른 회원과 동일합니다.");
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    users= userRepository.findByLoginId(userDto.getLoginId()).orElse(null);
    if(users == null){
      log.info("존재하지 않는 회원입니다.");
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    if((users.getLoginType() == LoginType.GOOGLE || 
        users.getLoginType() == LoginType.KAKAO)
        && users.getEmail().compareTo(userDto.getEmail()) !=0 ){
      log.info("Google, Kakao 회원은 이메일 변경이 불가능합니다.");
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);

    }
    if(userDto.getUserType().name().compareTo(users.getUserType().name()) == 0
    && userDto.getEmail().compareTo(users.getEmail()) == 0
    && userDto.getNickname().compareTo(users.getNickname()) == 0
    && userDto.getPhone().compareTo(users.getPhone()) == 0) {
      log.info("변경 가능한 정보들이 모두 일치하거나 다른 회원과 동일합니다.");
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }

    users.setUserType(userDto.getUserType());
    if((users.getLoginType() != LoginType.GOOGLE &&
        users.getLoginType() != LoginType.KAKAO)
        && users.getEmail().compareTo(userDto.getEmail()) !=0 ) {
      users.setEmail(userDto.getEmail());
    }
    users.setNickname(userDto.getNickname());
    users.setPhone(userDto.getPhone());
    users.setUpdatedAt(LocalDateTime.now());
    UsersEntity updateUser= userRepository.save(users);
    UserDto update=userMapper.toDto(updateUser);
    update.setPassword(null);
    return update;
  }

  @Override
  public boolean UpdatePassword(String userId, String oldPass, String newPass) {
    try {
      if (oldPass.compareTo(newPass) == 0) {
        log.info("기존 비밀번호와 새로운 비밀번호가 동일합니다.");
        throw new CustomException(ErrorCode.USER_BAD_REQUEST);
      }
      UsersEntity user = userRepository.findByLoginId(userId).orElse(null);
      if (user == null) {
        log.info("존재하지 않는 회원입니다.");
        throw new CustomException(ErrorCode.USER_BAD_REQUEST);
      }

      String encode = passwordEncoder.encode(newPass);
      user.setPassword(encode);
      userRepository.save(user);
      return true;
    }catch (Exception e){
      e.printStackTrace();
      log.info("비밀번호 변경 중 오류 발생");
      return false;
    }
  }

  @Override
  public boolean DeleteUser(UserDto userDto) {
    if(userRepository.deleteByLoginId(userDto.getLoginId())>0){
      return true;
    }
    return false;
  }
}
