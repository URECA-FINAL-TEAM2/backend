package com.beautymeongdang.global.jwt;

import com.beautymeongdang.domain.user.entity.User;
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
        // Access Token 생성
        String accessToken = jwtUtil.createAccessToken(
                user.getUserId().toString(),
                ACCESS_TOKEN_EXPIRE_TIME
        );

        // Refresh Token 생성
        String refreshToken = jwtUtil.createRefreshToken(
                user.getUserId().toString(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        // 토큰 암호화
        String encryptedAccessToken = CookieEncryption.encrypt(accessToken);
        String encryptedRefreshToken = CookieEncryption.encrypt(refreshToken);

        // Access Token 쿠키 설정 (암호화된 값만)
        response.addHeader("Set-Cookie",
                String.format("access_token=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                        encryptedAccessToken,
                        ACCESS_TOKEN_EXPIRE_TIME / 1000));

        // Refresh Token 쿠키 설정 (암호화된 값만)
        response.addHeader("Set-Cookie",
                String.format("refresh_token=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                        encryptedRefreshToken,
                        REFRESH_TOKEN_EXPIRE_TIME / 1000));

        // 클라이언트에 필요한 정보 반환 (원본 토큰은 sessionStorage에서 관리)
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("access_token", "Bearer " + accessToken); // 원본 토큰 (프론트에서 sessionStorage에 저장)
        tokenInfo.put("refresh_token", refreshToken);  // 리프레시 토큰 (프론트에서 sessionStorage에 저장)
        tokenInfo.put("userId", user.getUserId());
        tokenInfo.put("nickname", user.getNickname());

        return tokenInfo;
    }

    public boolean validateToken(String encryptedToken, String originalToken) {
        try {
            // 암호화된 토큰과 원본 토큰 검증
            String decryptedToken = CookieEncryption.decrypt(encryptedToken, originalToken);
            return jwtUtil.validateToken(decryptedToken);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    public Long getUserIdFromToken(String encryptedToken, String originalToken) {
        try {
            if (!validateToken(encryptedToken, originalToken)) {
                throw new IllegalArgumentException("Invalid token");
            }
            String decryptedToken = CookieEncryption.decrypt(encryptedToken, originalToken);
            String userIdString = jwtUtil.getUserId(decryptedToken);
            return Long.parseLong(userIdString);
        } catch (Exception e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }
}