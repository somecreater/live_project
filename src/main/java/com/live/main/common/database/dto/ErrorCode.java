package com.live.main.common.database.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  FAILURE_LOGIN(HttpStatus.BAD_REQUEST, "아이디나 비밀번호가 틀렸습니다."),
  LOGIN_LIMIT(HttpStatus.FORBIDDEN,
      "로그인 실패 횟수가 일정 이상 초과했습니다. 비밀번호 찾기나 아이디 찾기를 해주세요."),
  SIGN_BAD_REQUEST(HttpStatus.BAD_REQUEST,
      "회원 가입에 실패했습니다. 아이디나 휴대폰번호, 닉네임을 확인해보세요!"),
  USER_BAD_REQUEST(HttpStatus.BAD_REQUEST,
      "회원님의 잘못된 요청입니다. 입력한 정보를 다시한번 확인해 주세요."),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 입력한 정보를 다시한번 확인해 주세요."),

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),

  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
  NO_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 상태가 아닙니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
  ACCESS_LIMIT(HttpStatus.BAD_REQUEST,"요청 횟수가 너무 많습니다."),

  SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 오류 발생.");
  private final HttpStatus status;
  private final String message;
}
