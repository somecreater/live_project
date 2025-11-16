package com.live.main.user.service;

import lombok.Data;

import java.util.Map;

@Data
public class Oauth2User {
  public Map<String, Object> attributes;
  public String nameAttributeKey;
  public String email;
  public String nickname;
}
