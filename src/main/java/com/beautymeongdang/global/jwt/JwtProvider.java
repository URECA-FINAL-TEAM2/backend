package com.beautymeongdang.global.jwt;

import com.beautymeongdang.domain.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    private final JWTUtil jwtUtil;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 300L; // 300분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14L; // 2주

    public Map<String, Object> createTokens(User user, HttpServletResponse response) {
        // Access Token 생성 (userId, nickname만 포함)
        String accessToken = jwtUtil.createAccessToken(
                user.getUserId().toString(),
                ACCESS_TOKEN_EXPIRE_TIME
        );

        // Refresh Token 생성 (userId만 포함)
        String refreshToken = jwtUtil.createRefreshToken(
                user.getUserId().toString(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        // Access Token을 쿠키에 저장
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setMaxAge(ACCESS_TOKEN_EXPIRE_TIME.intValue() / 1000);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);

        // Refresh Token을 쿠키에 저장
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_EXPIRE_TIME.intValue() / 1000);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);

        // 응답 데이터 생성
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("access_token", accessToken);
        tokenInfo.put("userId", user.getUserId());
        tokenInfo.put("nickname", user.getNickname());

        return tokenInfo;
    }

    public boolean validateToken(String token) {
        try {
            // JWTUtil에 토큰 검증 메서드 추가 필요
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            // JWTUtil에서 userId를 추출하는 메서드 추가 필요
            String userIdString = jwtUtil.getUserId(token);
            return Long.parseLong(userIdString);
        } catch (Exception e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }
}