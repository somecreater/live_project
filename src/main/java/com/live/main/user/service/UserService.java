package com.live.main.user.service;

import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.entity.UsersEntity;
import com.live.main.user.database.mapper.UserMapper;
import com.live.main.user.database.repository.UserRepository;
import com.live.main.user.service.Interface.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  //사용자 아이디로 사용자 인증 객체 생성
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UsersEntity> optionalUsers= userRepository.findByLoginId(username);
    if(optionalUsers.isEmpty()){
      throw new UsernameNotFoundException("User not found with LoginID: " + username);
    }
    return new CustomUserDetails(optionalUsers.get());
  }

  @Override
  public UserDto RegisterUser(UserDto userDto) {
    UsersEntity users=new UsersEntity();
    if(userDto.getUserType() == null
      || userDto.getLoginId() == null
      || userDto.getNickname() == null
      || userDto.getPhone() == null
      || userDto.getLoginType() == null
      || userDto.getPassword() == null){
      log.info("회원 정보중 일부 누락된 정보 존재!");
      return null;
    }
    if(userRepository.existsByLoginIdOrPhoneOrNickname(
        userDto.getLoginId(),
        userDto.getPhone(),
        userDto.getNickname()
    )){
      log.info("아이디, 전화번호, 닉네임 중 하나가 이미 존재합니다.");
      return null;
    }

    users=userMapper.toEntity(userDto);
    UsersEntity newUser = userRepository.save(users);
    return userMapper.toDto(newUser);
  }

  @Override
  public UserDto LoginUser(String id, String pass) {
    return null;
  }

  @Override
  public UserDto GetUserInfo(UserDto userDto) {
    return null;
  }

  @Override
  public UserDto UpdateUser(UserDto userDto) {
    return null;
  }

  @Override
  public boolean UpdatePassword(UserDto userDto, String pass) {
    return false;
  }

  @Override
  public boolean DeleteUser(UserDto userDto) {
    return false;
  }
}
