package com.live.main.user.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.jwt.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

  @Autowired
  private JwtService jwtService;

  @Value("${spring.jwt.access_token_expiration}")
  private Long accessTokenExpiration;
  @Value("${spring.jwt.refresh_token_expiration}")
  private Long refreshTokenExpiration;
  @Value("${app.oauth2.redirect-uri}")
  private String redirectUri;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    OAuth2AuthenticationToken authenticationToken= (OAuth2AuthenticationToken) authentication;
    OAuth2User oAuth2User =  authenticationToken.getPrincipal();

    Map<String,Object> attributes=oAuth2User.getAttributes();
    String loginServer = authenticationToken.getAuthorizedClientRegistrationId();
    String userName = (String)attributes.get("username");

    Oauth2User customOauth2User = switch (loginServer) {
      case "google" -> GoogleUserService.google(userName, attributes);
      case "kakao" -> KakaoUserService.kakao(userName, attributes);
      default -> throw new CustomException(ErrorCode.USER_BAD_REQUEST);
    };

    String email = customOauth2User.getEmail();
    String auth = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining());

    String accessToken=jwtService.generatedAccessToken(email, auth);
    String refreshToken=jwtService.generatedRefreshToken(email, auth);

    ResponseCookie newAccessCookie=ResponseCookie.from("accessToken",accessToken)
        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(accessTokenExpiration)
        .build();
    ResponseCookie newRefreshCookie=ResponseCookie.from("refreshToken",refreshToken)
        .httpOnly(true).secure(true).sameSite("None").path("/").maxAge(refreshTokenExpiration)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE,newAccessCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());

    response.sendRedirect(redirectUri);

  }
}
