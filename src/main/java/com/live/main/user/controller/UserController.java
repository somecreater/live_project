package com.live.main.user.controller;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.user.database.dto.LoginRequest;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.jwt.JwtService;
import com.live.main.user.service.Interface.UserServiceInterface;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  @Value("${spring.jwt.access_token_expiration}")
  private Long accessTokenExpiration;

  @Value("${spring.jwt.refresh_token_expiration}")
  private Long refreshTokenExpiration;

  private final UserServiceInterface userService;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<?> UserRegister(@RequestBody UserDto userDto){
    log.info("[POST] /api/user/register - {}", userDto.getLoginId());
    Map<String,Object> result=new HashMap<>();

    UserDto newUser=userService.RegisterUser(userDto);
    if(newUser == null){
      result.put("result",false);
    }else{
      result.put("result",true);
      result.put("userID",newUser.getLoginId());
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/login")
  public ResponseEntity<?> UserLogin(@RequestBody LoginRequest loginRequest,
      HttpServletResponse response){
    log.info("[POST] /api/user/login - {}", loginRequest.getId());
    Map<String,Object> result=new HashMap<>();

    if(loginRequest.getId() == null || loginRequest.getPass() == null){
      throw new CustomException(ErrorCode.FAILURE_LOGIN);
    }

    UserDto login=userService.LoginUser(loginRequest.getId(), loginRequest.getPass());
    String accessToken = jwtService.generatedAccessToken(
        login.getLoginId(),
        login.getUserType().name());
    String refreshToken = jwtService.generatedRefreshToken(
        login.getLoginId(),
        login.getUserType().name());


    ResponseCookie newAccessCookie=ResponseCookie.from("accessToken",accessToken)
        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(accessTokenExpiration)
        .build();
    ResponseCookie newRefreshCookie=ResponseCookie.from("refreshToken",refreshToken)
        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(refreshTokenExpiration)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE,newAccessCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());
    result.put("result", true);

    return ResponseEntity.ok(result);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> UserLogout(@AuthenticationPrincipal CustomUserDetails principal,
      HttpServletResponse response){
    log.info("[POST] /api/user/logout - {}", principal.getUserid());

    String loginId= principal.getUserid();
    jwtService.deleteRefreshToken(loginId);

    ResponseCookie clearAccessCookie=ResponseCookie.from("accessToken","")
        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0)
        .build();
    ResponseCookie clearRefreshCookie=ResponseCookie.from("refreshToken","")
        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE,clearAccessCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());

    return ResponseEntity.ok().build();
  }

  @GetMapping("/info")
  public ResponseEntity<?> UserInfo(@AuthenticationPrincipal CustomUserDetails principal){
    log.info("[GET] /api/user/info/{} - ",principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    UserDto request=new UserDto();
    request.setLoginId(principal.getUserid());
    UserDto userInfo=userService.GetUserInfo(request);

    if(userInfo != null){
      result.put("result",true);
      result.put("user_info",userInfo);
    }else{
      result.put("result",false);
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> UserUpdate(@AuthenticationPrincipal CustomUserDetails principal,
      @RequestBody UserDto userDto){
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
