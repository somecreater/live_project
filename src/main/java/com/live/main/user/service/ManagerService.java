package com.live.main.user.service;

import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.entity.UserType;
import com.live.main.user.database.entity.UsersEntity;
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

import java.time.LocalDateTime;


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
        userList = userRepository.findByLoginIdContains(keyword, pageRequest).map(userMapper::toDto);
        break;
      case "nickname":
        userList = userRepository.findByNicknameContains(keyword, pageRequest).map(userMapper::toDto);
        break;
      case "email":
        userList = userRepository.findByEmailContains(keyword, pageRequest).map(userMapper::toDto);
        break;
      default:
        log.info("관리자 회원 목록 조회 시도 중 잘못된 검색 유형: {}", type);
    }

    return userList;
  }

  @Async("IOTaskExecutor")
  @Override
  public void ForceDeleteUser(Long userId) {
    /*
    회원의 타입에 따라서 다르게 처리(비동기로 처리, 오래걸림(나중에 알림으로 결과만 보내주기))
    추후 추가 가능성 있음
    1. 먼저 연관이 거의 없는 데이터 삭제(알림, 관리자 메시지)
    2. 연관이 적은 데이터 삭제(채널 커버, 프로필 이미지, 회원 구독 정보)
    3. 연관이 많은 데이터 삭제(게시글, 동영상 등)
    4. 채널 삭제
    5. 회원 삭제
     */
    UsersEntity user=userRepository.findById(userId).orElse(null);
    if(user==null) {
      log.info("관리자에 의한 회원 강제 탈퇴 시도 중 존재하지 않는 회원: {}", userId);
      return;
    }

    if(user.getUserType() == UserType.STREAMER){

    }else if(user.getUserType() == UserType.NORMAL){

    }else if(user.getUserType() == UserType.MANAGER){
      throw new IllegalArgumentException("관리자 계정은 강제 탈퇴할 수 없습니다.");
    }

  }

  @Override
  public boolean ReviewReportedContent(Long contentId, String contentType, String action) {

    return false;
  }

  @Override
  public void SendManagerMessage(String title, String content, String sender, String targetId){
    if(title.isBlank() || content.isBlank() || sender.isBlank() || targetId.isBlank()){
      log.info("관리자 메시지 전송 시도 중 잘못된 입력 - title: {}, content: {}, sender: {}, targetId: {}",
        title, content, sender, targetId);
      return;
    }
    ManagerMessageEvent managerMessageEvent=new ManagerMessageEvent(
      null, title, content, sender, targetId, false, LocalDateTime.now());
    publisher.publishEvent(managerMessageEvent);
  }
}
