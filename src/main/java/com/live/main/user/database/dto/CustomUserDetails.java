package com.live.main.user.database.dto;

import com.live.main.user.database.entity.UsersEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

  private final String userid;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(String userid, String password, String userType) {
    this.userid = userid;
    this.password = password;
    this.authorities = List.of(new SimpleGrantedAuthority(userType));
  }
  public CustomUserDetails(UsersEntity users){
    this.userid = users.getLoginId();
    this.password = null;
    this.authorities= List.of(new SimpleGrantedAuthority(users.getUserType().name()));
  }
  public CustomUserDetails(String userId, String auth){
    this.userid = userId;
    this.password = null;
    this.authorities= List.of(new SimpleGrantedAuthority(auth));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userid;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
