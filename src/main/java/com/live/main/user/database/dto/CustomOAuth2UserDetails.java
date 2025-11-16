package com.live.main.user.database.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2UserDetails implements OAuth2User {

  private Map<String, Object> attribute;
  private String nameKey;
  private UserDto user;

  public CustomOAuth2UserDetails( Map<String, Object> attributes, String nameKey, UserDto user){
    this.attribute = attributes;
    this.nameKey = nameKey;
    this.user = user;
  }

  @Override
  public Map<String, Object> getAttributes() { return attribute; }
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(()->user.getUserType().name()); }
  @Override
  public String getName() { return user.getNickname(); }
  public String getEmail(){ return user.getEmail(); }
}
