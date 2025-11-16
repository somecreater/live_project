package com.live.main.user.service;

import java.util.Map;


public class GoogleUserService extends Oauth2User {

  public GoogleUserService (Map<String, Object> attributes, String nameAttributeKey,
      String email, String nickname) {
    super.attributes = attributes;
    super.nameAttributeKey = nameAttributeKey;
    super.email = email;
    super.nickname = nickname;
  }
  public static GoogleUserService google(String userNameAttributeName,
      Map<String, Object> attributes){
    String email = (String) attributes.get("email");
    String nickname = (String) attributes.get("name");
    return new GoogleUserService(attributes,userNameAttributeName,email,nickname);
  }

}
