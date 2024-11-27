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
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
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

    public Set<String> getRoles(String token) {
        String rolesStr = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("roles", String.class);
        return new HashSet<>(Arrays.asList(rolesStr.split(",")));
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // Access Token 생성
    public String createAccessToken(String userId, String nickname, Set<String> roles, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("nickname", nickname)
                .claim("roles", String.join(",", roles))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String userId, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
