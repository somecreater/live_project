package com.live.main.user.service.Interface;


/**이메일 기능 (2025-11-21)*/
public interface MailServiceInterface {

  /**메일 전송*/
  public boolean sendMail(String toMail, String title, String content);
  /**인증번호 전송*/
  public boolean sendVerification(String mail);
  /**인증번호 확인*/
  public boolean CheckVerification(String mail, String code);
}
