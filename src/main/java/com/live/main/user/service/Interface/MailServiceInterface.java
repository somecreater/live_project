package com.live.main.user.service.Interface;


/**이메일 기능 (2025-11-21)*/
public interface MailServiceInterface {

  /**인증번호 생성 및 저장*/
  public String generatorCode(String mail);
  /**메일 전송*/
  public boolean sendMail(String toMail, String title, String content);
  /**인증번호 전송*/
  public boolean sendVerification(String mail);
  /**인증번호 확인*/
  public boolean CheckVerification(String mail, String code);
  /**아이디 찾기를 위한 이메일 전송*/
  public boolean sendMailForId(String mail);
  /**아이디 찾기를 위한 인증번호 확인*/
  public String searchId(String mail, String code);
  /**비밀번호 찾기를 위한 이메일 전송*/
  public boolean sendMailForPass(String UserId, String mail);
  /**비밀번호 찾기를 위한 인증번호 확인*/
  public boolean searchPass(String mail, String code);
}
