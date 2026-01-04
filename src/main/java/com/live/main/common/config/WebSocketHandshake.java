package com.live.main.common.config;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.database.repository.OnlineRepository;
import com.live.main.common.exception.CustomException;
import com.live.main.user.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandshake implements HandshakeInterceptor {

    //나중에 API 호출로 변경(분리시 삭제)
    private final JwtService jwtService;
    private final OnlineRepository onlineRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        String token = null;

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        attributes.put("accessToken", token);
                        break;
                    }
                }
            }
            if(jwtService.ValidationToken(token)){
                String memberId = jwtService.getUserId(token);
                String auth = jwtService.getAuth(token);

                attributes.put("memberId", memberId);
                attributes.put("auth", auth);
                attributes.put("token", token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        memberId,
                        token,
                        List.of(new SimpleGrantedAuthority(auth))
                );
                attributes.put("principal", authentication);

                return true;
            }else{
                log.error("[WS HANDSHAKE] ❌ Token validation failed");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            @Nullable Exception exception) {

        if (exception != null) {
            log.error("[WS HANDSHAKE] ❌ Handshake failed", exception);
        } else {
            log.info("[WS HANDSHAKE] ✅ Handshake completed successfully");
        }

    }
}
