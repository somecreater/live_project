package com.live.main.common.exception;

import com.live.main.common.database.dto.ErrorCode;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{
  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public HttpStatus getStatus() {
    return errorCode.getStatus();
  }

  public String getErrorCode() {
    return errorCode.name();
  }
}
