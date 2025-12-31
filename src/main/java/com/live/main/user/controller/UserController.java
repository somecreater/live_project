package com.live.main.user.controller;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.CoverServiceInterface;
import com.live.main.channel.service.Interface.PostServiceInterface;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.database.repository.OnlineRepository;
import com.live.main.common.exception.CustomException;
import com.live.main.profile.service.Interface.ProfileServiceInterface;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.user.database.dto.LoginRequest;
import com.live.main.user.database.dto.ResetPasswordRequest;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.jwt.JwtService;
import com.live.main.user.service.Interface.UserServiceInterface;
import com.live.main.video.service.Interface.VideoServiceInterface;
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
  private final ChannelServiceInterface channelService;
  private final VideoServiceInterface videoService;
  private final ProfileServiceInterface profileService;
  private final CoverServiceInterface coverService;
  private final PostServiceInterface postService;
  private final JwtService jwtService;

  private final OnlineRepository onlineRepository;

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
    log.info("[POST] /api/user/login - {}", loginRequest.getLoginId());
    Map<String,Object> result=new HashMap<>();

    if(loginRequest.getLoginId() == null || loginRequest.getPass() == null){
      throw new CustomException(ErrorCode.FAILURE_LOGIN);
    }

    UserDto login=userService.LoginUser(loginRequest.getLoginId(), loginRequest.getPass());
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
    onlineRepository.save(login.getLoginId(),"online");

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
    onlineRepository.delete(loginId);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/info")
  public ResponseEntity<?> UserInfo(@AuthenticationPrincipal CustomUserDetails principal){
    log.info("[GET] /api/user/info - {}",principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    UserDto userInfo=userService.GetUserInfo(principal.getUserid());

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
    log.info("[POST] /api/user/update - {}",principal.getUserid());
    Map<String,Object> result=new HashMap<>();
    if(principal.getUserid().compareTo(userDto.getLoginId()) != 0){
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }

    UserDto updateUser=userService.UpdateUser(userDto);
    if(updateUser == null){
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }else{
      result.put("result",true);
      result.put("newUser",updateUser);
    }

    return ResponseEntity.ok(result);
  }

  @PostMapping("/reset_password")
  public ResponseEntity<?> ResetPassword(@AuthenticationPrincipal CustomUserDetails principal,
      @RequestBody ResetPasswordRequest resetPasswordRequest){
    log.info("[POST] /api/user/reset_password - {}",principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    if(principal.getUserid().compareTo(resetPasswordRequest.getUserId()) != 0){
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    boolean update=userService.UpdatePassword(
        resetPasswordRequest.getUserId(),
        resetPasswordRequest.getOrg_pass(),
        resetPasswordRequest.getNew_pass());

    if(update){
      result.put("result", true);
      result.put("UserId", resetPasswordRequest.getUserId());
    }else{
      result.put("result",false);
      result.put("UserId", resetPasswordRequest.getUserId());
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> UserDelete(@AuthenticationPrincipal CustomUserDetails principal,
      @RequestBody LoginRequest loginRequest, HttpServletResponse response){
    log.info("[POST] /api/user/delete - {}",principal.getUserid());
    Map<String,Object> result=new HashMap<>();

    if(principal.getUserid().compareTo(loginRequest.getLoginId()) != 0){
      throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    }
    UserDto user=userService.LoginUser(loginRequest.getLoginId(), loginRequest.getPass());
    ChannelDto channelDto=channelService.getChannelInfoUser(user.getLoginId());
    boolean video_delete = videoService.VideoDeleteOnChannel(channelDto.getName());
    boolean cover_delete= coverService.cover_delete_on_channel(channelDto.getName());
    boolean channel_delete = channelService.deleteChannelOnUser(user.getLoginId());
    boolean profile_delete= profileService.profile_delete_onUser(user.getLoginId());
    boolean post_delete= postService.deletePostOnChannel(channelDto.getName());
    boolean subscription_delete_1= channelService.deleteSubscriptionOnChannel(channelDto.getName());
    boolean subscription_delete_2= channelService.deleteSubscriptionOnUser(user.getLoginId());
    boolean delete=userService.DeleteUser(user);

    if(video_delete && cover_delete && channel_delete
       && profile_delete && post_delete &&subscription_delete_1
       && subscription_delete_2 && delete){
      jwtService.deleteRefreshToken(user.getLoginId());
      ResponseCookie clearAccessCookie=ResponseCookie.from("accessToken","")
          .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0)
          .build();
      ResponseCookie clearRefreshCookie=ResponseCookie.from("refreshToken","")
          .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(0)
          .build();

      response.addHeader(HttpHeaders.SET_COOKIE,clearAccessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie.toString());

      result.put("result",true);
      result.put("UserId",user.getLoginId());
    }else{
      result.put("result",false);
      result.put("UserId",user.getLoginId());
    }
    return ResponseEntity.ok(result);
  }

}
