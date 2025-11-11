package com.live.main.user.controller;

import com.live.main.user.database.dto.LoginRequest;
import com.live.main.user.database.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  @PostMapping("/register")
  public ResponseEntity<?> UserRegister(@RequestBody UserDto userDto){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/login")
  public ResponseEntity<?> UserLogin(@RequestBody LoginRequest loginRequest){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> UserLogout(){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @GetMapping("/info/{loginId}")
  public ResponseEntity<?> UserInfo(){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> UserUpdate(){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/reset_password")
  public ResponseEntity<?> ResetPassword(){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> UserDelete(){
    Map<String,Object> result=new HashMap<>();

    return ResponseEntity.ok(result);
  }

}
