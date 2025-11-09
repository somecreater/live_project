package com.live.main.user.jwt;


import com.live.main.user.database.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  private final static String HEADER_AUTHORIZATION = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";
  private final long ACCESS_TOKEN_MAX_AGE_SECONDS= 15 * 60L;

  //토큰 검증 미적용
  @Override
  public boolean shouldNotFilter(HttpServletRequest request){
    return !request.getRequestURI().startsWith("/api/");
  }

  //JWT 토큰 필터
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
      String accessToken =null;

      accessToken = extractCookie(request,"accessToken");

      if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
        accessToken = authorizationHeader.substring(TOKEN_PREFIX.length());
      }

      //해당 엑세스 토큰이 유효한 경우
      if(accessToken != null && jwtService.ValidationToken(accessToken)){
        String userLoginId = jwtService.getUserId(accessToken);
        String auth= jwtService.getAuth(accessToken);
        setAuthentication(userLoginId, auth);
        log.info("Access token valid - authentication set");
      }
      //해당 엑세스 토큰이 만료된 경우
      else{
        log.info("Access token expired; attempting refresh inside filter");
        String refreshToken=extractCookie(request,"refreshToken");

        if (refreshToken == null || !jwtService.ValidationRefreshToken(refreshToken)) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }


        Claims claims = jwtService.getClaims(refreshToken); // get from expired token
        String userLoginId = claims.get("Userid", String.class);
        String auth = claims.get("auth", String.class);

        String newAccessToken= jwtService.generatedAccessToken(userLoginId,auth);

        ResponseCookie cookie = ResponseCookie.from("accessToken", newAccessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(ACCESS_TOKEN_MAX_AGE_SECONDS)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        setAuthentication(userLoginId,auth);
        log.info("Access token refreshed via refresh token");
      }

    } catch (Exception e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      log.error("Error JWT {}",e.getMessage());
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void setAuthentication(String userLoginId, String auth) {
    CustomUserDetails customUserDetails = new CustomUserDetails(userLoginId, auth);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        customUserDetails, null, customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String extractCookie(HttpServletRequest req, String name) {
    if (req.getCookies() == null) return null;
    for (Cookie c : req.getCookies()) {
      if (name.equals(c.getName())) {
        return c.getValue();
      }
    }
    return null;
  }

}
