package com.live.common.config;

import com.live.user.jwt.JwtFilter;
import com.live.user.jwt.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig{

  @Value("${app.security.allowed_origin}")
  private List<String> allowedOrigins;

  @Value("${app.security.permit_all_list}")
  private List<String> permitAllList;

  @Value("${app.security.permit_all_num}")
  private Integer PermitAllNum;

  @Value("${app.security.is_cors}")
  private boolean IsCors;

  private final JwtService jwtService;

  //Spring Security 미적용 메소드(JS, 이미지 같은 리소스)
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web)-> web.ignoring()
        .requestMatchers("/index.html","/assets/**", "/favicon.ico", "/*.png", "/*.svg", "/*.jpg", "/*.html",
            "/*.css", "/*.js","/asset-manifest.json","/static/**");
  }

  //CORS 설정 메소드
  public CorsConfigurationSource  corsConfigurationSource(){
    CorsConfiguration corsConfiguration= new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(allowedOrigins);
    corsConfiguration.setAllowedMethods(List.of("*"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setMaxAge(3600L);
    corsConfiguration.setExposedHeaders(List.of("Authorization"));
    corsConfiguration.addExposedHeader("Set-Cookie");
    UrlBasedCorsConfigurationSource source= new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**",corsConfiguration);
    return source;
  }

  //Spring Security 설정 메소드
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    if(IsCors){
      http.cors(Customizer.withDefaults());
    }
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement((security)->
            security.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((security)
            ->security.requestMatchers(permitAllList.toArray(new String[PermitAllNum])).permitAll()
            .requestMatchers("/api/**").authenticated())
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable);

    http.exceptionHandling(exception->
        exception
            .authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler((req, res, exc) -> {throw  exc;}));

    /*
    ---추후 설정(2025/11/6)---

    http.oauth2Login((oauth2)->oauth2
        .loginPage("/user/login")
        .userInfoEndpoint((userInfo)->userInfo));
    */

    http.addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class);


    return http.build();
  }

  //패스워드 인코더 설정
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  //인증 예외 처리
  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(){
    return new CustomAuthenticationEntryPoint();
  }

  // ---추후 설정(2025/11/6)---

  //소셜 로그인 설정

  //소셜 로그인 성공 핸들러

  //소셜 로그인 실패 핸들러

}
