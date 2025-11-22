package com.live.main.user.controller;

import com.live.main.user.database.dto.EmailVerification;
import com.live.main.user.database.dto.RequestPassword;
import com.live.main.user.database.dto.SendEmail;
import com.live.main.user.service.Interface.MailServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController {

  private final MailServiceInterface mailService;

  @PostMapping("/send_verification")
  public ResponseEntity<?> sendVerification(@RequestBody SendEmail sendEmail){
    log.info("[POST] /public/mail/send_verification - {}", sendEmail.getEmail());
    Map<String,Object> result=new HashMap<>();
    if(mailService.sendVerification(sendEmail.getEmail())){
      result.put("result",true);
      result.put("email",sendEmail.getEmail());
    }else{
      result.put("result",false);
      result.put("email",sendEmail.getEmail());
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/verification")
  public ResponseEntity<?> verification(@RequestBody EmailVerification emailVerification){
    log.info("[POST] /public/mail/verification - {}", emailVerification.getEmail());
    Map<String,Object> result=new HashMap<>();
    if(mailService.CheckVerification(emailVerification.getEmail(), emailVerification.getToken())){
      result.put("result",true);
      result.put("email",emailVerification.getEmail());
    }else{
      result.put("result",false);
      result.put("email",emailVerification.getEmail());
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/SearchId")
  public ResponseEntity<?> SearchId(@RequestBody SendEmail sendEmail){
    log.info("[POST] /public/mail/SearchId - {}",sendEmail.getEmail());
    Map<String,Object> result=new HashMap<>();
    mailService.sendMailForId(sendEmail.getEmail());

    result.put("message", "처리되었습니다.");
    result.put("email",sendEmail.getEmail());

    return ResponseEntity.ok(result);
  }

  @PostMapping("/GetId")
  public ResponseEntity<?> getVerificationId(@RequestBody EmailVerification emailVerification){
    log.info("[POST] /public/mail/GetId - {}", emailVerification.getEmail());
    Map<String,Object> result=new HashMap<>();
    String LoginId=mailService.searchId(emailVerification.getEmail(), emailVerification.getToken());
    if(LoginId != null){
      result.put("message","처리되었습니다.");
      result.put("login_id",LoginId);
    }else{
      result.put("message","처리되었습니다.");
      result.put("login_id","확인중 입니다.");
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/SearchPassword")
  public ResponseEntity<?> SearchPassword(@RequestBody RequestPassword requestPassword){
    log.info("[POST] /public/mail/SearchPassword - {}",requestPassword.getLoginId());
    Map<String,Object> result=new HashMap<>();
    mailService.sendMailForPass(requestPassword.getLoginId(), requestPassword.getEmail());

    result.put("message", "처리되었습니다.");
    result.put("email", requestPassword.getEmail());

    return ResponseEntity.ok(result);
  }

  @PostMapping("/GetPassword")
  public ResponseEntity<?> getVerificationPass(@RequestBody EmailVerification emailVerification){
    log.info("[POST] /public/mail/GetPassword - {}",emailVerification.getEmail());
    Map<String,Object> result=new HashMap<>();
    if(mailService.searchPass(emailVerification.getEmail(), emailVerification.getToken())){
      result.put("message", "메일로 새로운 비밀번호를 전송합니다.");
    }

    return ResponseEntity.ok(result);
  }
}
