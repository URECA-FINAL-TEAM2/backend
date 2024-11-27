package com.beautymeongdang.global.oauth2;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.global.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

@Component
@AllArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 300L; // 300분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14L; // 2주

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = customUserDetails.getUserId();
        String nickname = customUserDetails.getName();

        Set<String> roles = customUserDetails.getUserDTO().getRoles();

        // Access Token 생성 (userId, nickname, roles 포함)
        String accessToken = jwtUtil.createAccessToken(
                userId.toString(),
                nickname,
                roles,
                ACCESS_TOKEN_EXPIRE_TIME
        );
        System.out.println("Access Token 생성: " + accessToken);

        // Refresh Token 생성 (userId만 포함)
        String refreshToken = jwtUtil.createRefreshToken(userId.toString(), REFRESH_TOKEN_EXPIRE_TIME);
        System.out.println("Refresh Token 생성: " + refreshToken);

        // Refresh Token을 쿠키에 저장
        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        // Access Token을 JSON 응답으로 전송
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("access_token", accessToken);
        responseData.put("userId", userId);
        responseData.put("nickname", nickname);
        responseData.put("roles", roles);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
        // register.html로 리다이렉트
        response.sendRedirect("/register.html");
    }

    private Cookie createRefreshTokenCookie(String value) {
        Cookie cookie = new Cookie("refresh_token", value);
        cookie.setMaxAge(REFRESH_TOKEN_EXPIRE_TIME.intValue() / 1000); // 쿠키 만료 시간을 초 단위로 설정
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setHttpOnly(true); // JavaScript에서 접근 불가
        return cookie;
    }
}