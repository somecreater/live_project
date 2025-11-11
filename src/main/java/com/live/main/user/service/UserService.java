package com.live.main.user.service;

import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.entity.UsersEntity;
import com.live.main.user.database.mapper.UserMapper;
import com.live.main.user.database.repository.UserRepository;
import com.live.main.user.jwt.JwtService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServiceInterface, UserDetailsService {

  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  //사용자 아이디로 사용자 인증 객체 생성
  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UsersEntity> optionalUsers= userRepository.findByLoginId(username);
    if(optionalUsers.isEmpty()){
      throw new UsernameNotFoundException("User not found with LoginID: " + username);
    }
    return new CustomUserDetails(optionalUsers.get());
  }

  //회원 등록 기능
  @Transactional
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

    userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    userDto.setCreatedAt(LocalDateTime.now());
    userDto.setUpdatedAt(LocalDateTime.now());
    users=userMapper.toEntity(userDto);
    UsersEntity newUser = userRepository.save(users);
    return userMapper.toDto(newUser);
  }

  //로그인 기능
  @Transactional(readOnly = true)
  @Override
  public UserDto LoginUser(String id, String pass) {
    UsersEntity loginUser=userRepository.findByLoginId(id).orElse(null);
    if(loginUser == null){
      throw new IllegalArgumentException("아이디나 비밀번호가 틀렸습니다.");
    }
    if(!passwordEncoder.matches(pass, loginUser.getPassword())){
      throw new IllegalArgumentException("아이디나 비밀번호가 틀렸습니다.");
    }

    return userMapper.toDto(loginUser);
  }

  // 현재는 아이디만 사용하지만 나중에는 채널명 등 여러 정보를 이용해서
  // 회원정보를 가지고 올 수도 있다.
  // 회원 정보 가져오기 기능
  @Transactional(readOnly = true)
  @Override
  public UserDto GetUserInfo(UserDto userDto) {
    UsersEntity usersEntity = null;
    String LoginId = userDto.getLoginId();
    if(LoginId != null){
      usersEntity = userRepository.findByLoginId(LoginId).orElse(null);
    }

    if(usersEntity == null){
      return null;
    }
    return userMapper.toDto(usersEntity);
  }

  //회원 정보 수정 기능
  @Override
  public UserDto UpdateUser(UserDto userDto) {
    
    return null;
  }

  //비밀번호 수정 기능
  @Override
  public boolean UpdatePassword(UserDto userDto, String pass) {
    return false;
  }

  //회원 탈퇴 기능
  @Override
  public boolean DeleteUser(UserDto userDto) {
    return false;
  }
}
