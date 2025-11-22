package com.live.main.user.controller;

import com.live.main.user.database.dto.EmailVerification;
import com.live.main.user.database.dto.SendEmail;
import com.live.main.user.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController {

  private final MailService mailService;

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
}
