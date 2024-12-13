package com.beautymeongdang.global.jwt;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class JWTUtil {
    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUserId(String token) {
        try {
            String userIdStr = Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload().get("userId", String.class);
            if (userIdStr == null || userIdStr.equals("null")) {
                return null;
            }
            return userIdStr;
        } catch (Exception e) {
            log.warn("Failed to get userId from token", e);
            return null;
        }
    }


    public Boolean isExpired(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            log.warn("Failed to check token expiration", e);
            return true;
        }
    }

    public String createAccessToken(String userId, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String userId, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // 토큰이 null이거나 비어있는 경우
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            // 만료 및 서명 확인
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            // 토큰 만료 여부 추가 확인
            return !isExpired(token);

        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
