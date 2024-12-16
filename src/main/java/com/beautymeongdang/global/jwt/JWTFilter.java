package com.beautymeongdang.global.jwt;

import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Authorization 헤더에서 토큰 확인
            String accessToken = request.getHeader("Authorization");
            Cookie accessTokenCookie = WebUtils.getCookie(request, "access_token");

            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                String token = accessToken.substring(7);
                String encryptedToken = accessTokenCookie != null ? accessTokenCookie.getValue() : null;

                if (encryptedToken != null) {
                    processAccessToken(token, encryptedToken);
                }
            } else {
                processRefreshToken(request, response);
            }
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private void processAccessToken(String token, String encryptedToken) {
        try {
            if (!jwtProvider.validateToken(encryptedToken, token)) {
                log.debug("Token validation failed");
                return;
            }

            Long userId = jwtProvider.getUserIdFromToken(encryptedToken, token);
            User user = userRepository.findByIdWithRoles(userId).orElse(null);

            if (user != null) {
                processTokenWithUser(user);
            }
        } catch (Exception e) {
            log.error("Failed to process access token", e);
        }
    }

    private void processRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie refreshTokenCookie = WebUtils.getCookie(request, "refresh_token");
            if (refreshTokenCookie == null) {
                log.debug("No refresh token cookie found");
                return;
            }

            String encryptedRefreshToken = refreshTokenCookie.getValue();

            // 원본 refresh 토큰 쿠키 가져오기
            Cookie originalRefreshTokenCookie = WebUtils.getCookie(request, "original_refresh_token");
            if (originalRefreshTokenCookie == null) {
                log.debug("No original refresh token cookie found");
                return;
            }

            String originalRefreshToken = originalRefreshTokenCookie.getValue();

            // 토큰 검증
            if (!jwtProvider.validateToken(encryptedRefreshToken, originalRefreshToken)) {
                log.debug("Refresh token validation failed");
                return;
            }

            Long userId = jwtProvider.getUserIdFromToken(encryptedRefreshToken, originalRefreshToken);
            if (userId == null) {
                log.debug("No userId in refresh token");
                return;
            }

            User user = userRepository.findByIdWithRoles(userId).orElse(null);
            if (user == null) {
                log.debug("User not found for refresh token");
                return;
            }

            // 새로운 토큰 발급
            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);
            log.debug("Created new tokens for user: {}", user.getUserId());

            processTokenWithUser(user);

        } catch (Exception e) {
            log.error("Failed to process refresh token: {}", e.getMessage());
        }
    }

    private void processTokenWithUser(User user) {
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customOAuth2User,
                null,
                customOAuth2User.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}