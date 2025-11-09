package com.live.main.user.jwt;

import com.live.main.user.database.dto.RefreshTokenDto;
import com.live.main.user.database.entity.RefreshTokenEntity;
import com.live.main.user.database.mapper.RefreshTokenMapper;
import com.live.main.user.database.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Log4j2
@Service
public class JwtService {

  private SecretKey secretKey;

  @Value("${spring.jwt.access_token_expiration}")
  private Long accessTokenExpiration;

  @Value("${spring.jwt.refresh_token_expiration}")
  private Long refreshTokenExpiration;

  private final RedisTemplate<String, String> redisTemplate;
  private static final String PREFIX = "RT:";

  private final RefreshTokenRepository refreshTokenRepository;
  private final RefreshTokenMapper refreshTokenMapper;
  //JWT 서비스 생성자(비밀키 생성)
  public JwtService(@Value("${spring.jwt.secret}")String secret,
      RedisTemplate<String, String> redisTemplate,
      RefreshTokenRepository refreshTokenRepository,
      RefreshTokenMapper refreshTokenMapper){
    secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm());
    this.refreshTokenRepository=refreshTokenRepository;
    this.refreshTokenMapper=refreshTokenMapper;
    this.redisTemplate=redisTemplate;
  }

  //JWT 생성(Access)
  public String generatedAccessToken(String UserId, String auth){
    return Jwts.builder().subject(UserId).claim("Userid", UserId).claim("auth", auth)
        .claim("tokenType", "ACCESS") .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+accessTokenExpiration))
        .signWith(secretKey).compact();
  }

  //JWT 생성(Refresh)
  public String generatedRefreshToken(String UserId, String auth){
    return Jwts.builder().subject(UserId).claim("Userid", UserId).claim("auth", auth)
        .claim("tokenType", "REFRESH") .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis()+refreshTokenExpiration))
        .signWith(secretKey).compact();
  }

  //JWT 유효성 검증
  public boolean ValidationToken(String token){
    try{
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.info("Token expired");
      return false;
    }  catch (JwtException e) {
      log.info("Invalid token: {}", e.getMessage());
      return false;
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public Claims getClaims(String token) {
    try {
      return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      log.info("Expired token - returning claims");
      return e.getClaims();
    } catch (JwtException e) {
      log.error("Failed to parse token claims: {}", e.getMessage());
      throw e;
    }
  }

  //Claims 정보 가져오기(회원 아이디)
  public String getUserId(String token){
    Claims claims= getClaims(token);
    if(claims == null || claims.get("Userid")==null){
      throw new RuntimeException("회원 아이디가 존재하지 않습니다.");
    }
    return claims.get("Userid").toString();
  }

  //Claims 정보 가져오기(회원 권한)
  public String getAuth(String token){
    Claims claims= getClaims(token);
    if(claims == null || claims.get("auth")==null){
      throw new RuntimeException("권한 정보가 존재하지 않습니다.");
    }
    return claims.get("auth").toString();
  }


  //Refresh JWT 삭제
  @Transactional
  public void deleteRefreshToken(String userId){
    try{
      long deleteNum = refreshTokenRepository.deleteByLoginId(userId);
      if(deleteNum == 0 ){
        log.info("Refresh token not exist for user: {}", userId);
      }else{
        log.info("Refresh token deleted for user: {}", userId);
      }

    } catch (Exception e) {
      log.error("Refresh token delete error", e);
    }
  }

  //Refresh JWT 검증
  public boolean ValidationRefreshToken(String token){
    try {
      RefreshTokenEntity RefreshToken = refreshTokenRepository.findByToken(token).orElse(null);

      if (RefreshToken == null) {
        return false;
      }
      if (!ValidationToken(token)) {
        deleteRefreshToken(RefreshToken.getLoginId());
        return false;
      }

      String loginId = getUserId(token);
      String auth = getAuth(token);
      if (RefreshToken.getLoginId().compareTo(loginId) != 0 || RefreshToken.getAuth()
          .compareTo(auth) != 0) {
        log.info("RefreshToken Validation False - mismatch data");
        return false;
      }

      return true;
    } catch (Exception e) {
      log.error("ValidationRefreshToken error", e);
      return false;
    }
  }

  //Refresh JWT 생성(DB(또는 Memory DB) 저장)
  @Transactional
  public RefreshTokenDto generateRefreshToken(String userId, String auth){
    String refreshToken=generatedRefreshToken(userId,auth);
    Instant instant= Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken)
        .getPayload().getExpiration().toInstant();

    RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
    tokenEntity.setToken(refreshToken);
    tokenEntity.setAuth(auth);
    tokenEntity.setLoginId(userId);
    tokenEntity.setExpiryDate(instant);

    RefreshTokenEntity refreshTokenEntity=refreshTokenRepository.save(tokenEntity);
    return refreshTokenMapper.toDto(refreshTokenEntity);
  }
}
