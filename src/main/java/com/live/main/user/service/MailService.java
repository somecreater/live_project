package com.live.main.user.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.common.service.Interface.CommonServiceInterface;
import com.live.main.user.database.entity.UsersEntity;
import com.live.main.user.database.repository.EmailVerificationRepository;
import com.live.main.user.database.repository.UserRepository;
import com.live.main.user.service.Interface.MailServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements MailServiceInterface {

  @Value("${app.verification-limit}")
  private int LIMIT_EMAIL_NUM;
  private final String VERIFICATION_TITLE="님 인증코드를 전송하였습니다";
  private final String VERIFICATION_CONTENT="인증코드 입니다. 해당 코드는 유출하면 안됩니다.";

  private final JavaMailSender mailSender;
  private final EmailVerificationRepository emailVerificationRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CommonServiceInterface commonService;

  @Value("${spring.mail.username}")
  private String FromMail;
  @Value("${app.verification-length}")
  private Integer VERIFICATION_CODE_LENGTH;
  @Value("${app.verification-range}'")
  private String VERIFICATION_CODE_RANGE;

  @Async("IOTaskExecutor")
  @Retryable(
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000, multiplier = 2)
  )
  @Override
  public boolean sendMail(String toMail, String title, String content) {
    try{
      int count= emailVerificationRepository.limit_save(toMail);
      if(count == Integer.MAX_VALUE){
        throw new CustomException(ErrorCode.ACCESS_LIMIT);
      }
      SimpleMailMessage message= new SimpleMailMessage();
      message.setFrom(FromMail);
      message.setTo(toMail);
      message.setSubject(title+" "+count+" 번째 시도(제한 횟수:"+LIMIT_EMAIL_NUM+")");
      message.setText(content);
      mailSender.send(message);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public String generatorCode(){
    SecureRandom random = new SecureRandom();
    StringBuilder code = new StringBuilder(VERIFICATION_CODE_LENGTH);

    for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
      int index = random.nextInt(VERIFICATION_CODE_RANGE.length());
      code.append(VERIFICATION_CODE_RANGE.charAt(index));
    }

    return code.toString();
  }

  @Override
  public boolean sendVerification(String mail) {
    try {
      if(userRepository.findByEmail(mail).isPresent()){
        return false;
      }
      String code=generatorCode();
      emailVerificationRepository.save(mail, code);

      String title=mail+VERIFICATION_TITLE;
      String content=VERIFICATION_CONTENT+"\n\n\n\n"+code;

      return sendMail(mail, title, content);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean CheckVerification(String mail, String code) {
    String savedCode= emailVerificationRepository.get(mail);
    if(savedCode.compareTo(code)==0) {
      log.info("{}님 이메일 인증 성공",mail);
      emailVerificationRepository.delete(mail);
      return true;
    }else{
      log.info("{}님이 코드 불일치로 이메일 인증에 실패하였습니다.",mail);
      return false;
    }
  }

  @Override
  @Transactional
  public boolean sendMailForId(String mail){
    try {
      UsersEntity user = userRepository.findByEmail(mail).orElse(null);
      if (mail.trim().isEmpty() || user == null) {
        log.info("{} 해당 메일을 찾을 수 없습니다.",mail);
        return false;
      }

      String code=generatorCode();
      emailVerificationRepository.save(mail, code);
      String Title = mail + VERIFICATION_TITLE + "(Login ID Search)";
      String Content = VERIFICATION_CONTENT + "\n\n\n\n" + code;
      if(sendMail(mail, Title, Content)){
        log.info("{} 에 해당하는 유저아이디 저장",mail);
        emailVerificationRepository.saveMailCache(mail,user.getLoginId());
        return true;
      }
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public String searchId(String mail, String code){
    String LoginId="";
    if(CheckVerification(mail, code)){
      LoginId=emailVerificationRepository.getMailCache(mail);
      LoginId= commonService.maskData(LoginId);
    }
    return LoginId;
  }

  @Override
  @Transactional
  public boolean sendMailForPass(String UserId, String mail){
    UsersEntity user = userRepository.findByEmail(mail).orElse(null);
    if (mail.trim().isEmpty() || user == null) {
      return false;
    }else if(!Objects.equals(user.getLoginId(), UserId)){
      return false;
    }

    String code=generatorCode();
    emailVerificationRepository.save(mail, code);
    String Title = mail + VERIFICATION_TITLE + "(Login Password Search)";
    String Content = VERIFICATION_CONTENT + "\n\n\n\n" + code;

    return sendMail(mail, Title, Content);
  }

  @Override
  public boolean searchPass(String mail, String code){
    try {
      if (CheckVerification(mail, code)) {
        String newPassword=generatorCode();
        String encode=passwordEncoder.encode(newPassword);
        userRepository.updatePasswordByEmail(encode,mail);
        String Title = "새로운 비밀번호 입니다. 외부로 유출하지 마세요.";
        String Content = "외부 유출 금지!!!! 첫 로그인 이후 바로 비밀번호 수정 권장! \n\n\n\n\n"+newPassword;

        return sendMail(mail,Title,Content);
      }else{
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
