package com.live.main.user.service;

import java.util.Map;

public class KakaoUserService extends Oauth2User {

  public KakaoUserService (Map<String, Object> attributes, String nameAttributeKey,
      String email, String nickname) {
    super.attributes = attributes;
    super.nameAttributeKey = nameAttributeKey;
    super.email = email;
    super.nickname = nickname;
  }

  public static KakaoUserService kakao(String userNameAttributeName,
      Map<String, Object> attributes){
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    String email = (String) kakaoAccount.get("email");
    Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
    String nickname = (String) kakaoProfile.get("nickname");

    return new KakaoUserService(attributes, userNameAttributeName, email, nickname);
  }
}
