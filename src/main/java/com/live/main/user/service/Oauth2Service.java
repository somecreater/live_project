package com.live.main.user.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomOAuth2UserDetails;
import com.live.main.user.database.dto.UserDto;
import com.live.main.user.database.entity.LoginType;
import com.live.main.user.database.entity.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class Oauth2Service extends DefaultOAuth2UserService {

  @Autowired
  private UserService userService;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
    OAuth2User oAuth2User = super.loadUser(userRequest);

    Map<String,Object> oAuthAttributes=oAuth2User.getAttributes();
    String loginServer= userRequest.getClientRegistration().getRegistrationId();
    String userName=userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

    Oauth2User CustomOauth2User = switch (loginServer) {
      case "google" -> GoogleUserService.google(userName, oAuthAttributes);
      case "kakao" -> KakaoUserService.kakao(userName, oAuthAttributes);
      default -> throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    };

    String email = CustomOauth2User.getEmail();

    UserDto user= userService.GetUserInfoByEmail(email);
    if(user == null){
      UserDto newUserDto=OAuth2UserDto(loginServer, CustomOauth2User);
      user=userService.RegisterUser(newUserDto);
    }
    return new CustomOAuth2UserDetails(oAuthAttributes, userName, user);
  }

  public UserDto OAuth2UserDto(String loginServer, Oauth2User oauth2User){
    UserDto userDto=new UserDto();
    userDto.setLoginId(oauth2User.getEmail());
    userDto.setEmail(oauth2User.getEmail());
    userDto.setEmailVerification(true);
    userDto.setPassword(bCryptPasswordEncoder.encode(UUID.randomUUID().toString()));
    userDto.setPhone(null);
    userDto.setNickname(loginServer+"_"+oauth2User.getNickname());
    if(loginServer.contains("google")){
      userDto.setLoginType(LoginType.GOOGLE);
    }else if(loginServer.contains("kakao")){
      userDto.setLoginType(LoginType.KAKAO);
    }
    userDto.setUserType(UserType.NORMAL);
    return userDto;
  }
}
