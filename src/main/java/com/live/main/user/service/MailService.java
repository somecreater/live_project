package com.live.main.user.service;

import com.live.main.user.database.repository.EmailVerificationRepository;
import com.live.main.user.service.Interface.MailServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements MailServiceInterface {

  private final String VERIFICATION_TITLE="님 인증코드를 전송하였습니다";
  private final String VERIFICATION_CONTENT="인증코드 입니다. 해당 코드는 유출하면 안됩니다.";

  private final JavaMailSender mailSender;
  private final EmailVerificationRepository emailVerificationRepository;

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
      SimpleMailMessage  message= new SimpleMailMessage();
      message.setFrom(FromMail);
      message.setTo(toMail);
      message.setSubject(title);
      message.setText(content);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean sendVerification(String mail) {
    try {
      SecureRandom random = new SecureRandom();
      StringBuilder code = new StringBuilder(VERIFICATION_CODE_LENGTH);

      for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
        int index = random.nextInt(VERIFICATION_CODE_RANGE.length());
        code.append(VERIFICATION_CODE_RANGE.charAt(index));
      }

      emailVerificationRepository.save(mail, code.toString());
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
}
