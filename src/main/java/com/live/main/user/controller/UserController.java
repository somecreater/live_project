package com.live.main.user.controller;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.LoginRequest;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.jwt.JwtService;
import com.live.main.user.service.Interface.UserServiceInterface;
import jakarta.servlet.http.HttpServletResponse;
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

  private final UserServiceInterface userService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<?> UserRegister(@RequestBody UserDto userDto){
    Map<String,Object> result=new HashMap<>();

    UserDto newUser=userService.RegisterUser(userDto);
    if(newUser == null){
      result.put("result",false);
    }else{
      result.put("result",true);
      result.put("user",newUser);
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/login")
  public ResponseEntity<?> UserLogin(@RequestBody LoginRequest loginRequest,
      HttpServletResponse response){
    Map<String,Object> result=new HashMap<>();
    if(loginRequest.getId() == null || loginRequest.getPass() == null){
      throw new CustomException(ErrorCode.FAILURE_LOGIN);
    }

    UserDto login=userService.LoginUser(loginRequest.getId(), loginRequest.getPass());
    String accessToken = jwtService.generatedAccessToken(
        login.getLoginId(),
        login.getUserType().name());
    String RefreshToken = jwtService.generatedRefreshToken(
        login.getLoginId(),
        login.getUserType().name());


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
