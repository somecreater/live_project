package com.live.main.user.service;

import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.mapper.UserMapper;
import com.live.main.user.database.repository.UserRepository;
import com.live.main.user.service.Interface.ManagerServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerService implements ManagerServiceInterface {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  private final ApplicationEventPublisher publisher;

  @Override
  public Page<UserDto> GetUserList(int page, int size, String type, String keyword) {
    PageRequest pageRequest= PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdAt"));
    Page<UserDto> userList= null;
    switch (type){
      case "all":
        userList= userRepository.findAll(pageRequest).map(userMapper::toDto);
        break;
      case "loginId":
        userList = userRepository.findByLoginIdLike(keyword, pageRequest).map(userMapper::toDto);
        break;
      case "nickname":
        userList = userRepository.findByNicknameLike(keyword, pageRequest).map(userMapper::toDto);
        break;
      case "email":
        userList = userRepository.findByEmailLike(keyword, pageRequest).map(userMapper::toDto);
        break;
      default:
        log.info("관리자 회원 목록 조회 시도 중 잘못된 검색 유형: {}", type);
    }

    return userList;
  }

  @Async("IOTaskExecutor")
  @Override
  public void ForceDeleteUser(Long userId) {
    //회원의 타입에 따라서 다르게 처리(비동기로 처리, 오래걸림(나중에 알림으로 결과만 보내주기))

  }

  @Override
  public boolean ReviewReportedContent(Long contentId, String contentType, String action) {

    return false;
  }

  @Override
  public void SendManagerMessage(ManagerMessageEvent managerMessageEvent){

  }
}
