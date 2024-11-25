package com.beautymeongdang.global.jwt;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.domain.user.dto.UserDTO;
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

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 Access Token 확인
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            processToken(accessToken);
        } else {
            // Refresh Token으로부터 새로운 Access Token 발급 시도
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String refreshToken = findRefreshToken(cookies);
                if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
                    String username = jwtUtil.getUsername(refreshToken);
                    // 새로운 Access Token 생성
                    String newAccessToken = jwtUtil.createAccessToken(username, "ROLE_USER", 1000 * 60 * 30L);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                    processToken(newAccessToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String findRefreshToken(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh_token")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void processToken(String token) {
        try {
            if (!jwtUtil.isExpired(token)) {
                String username = jwtUtil.getUsername(token);
                String role = jwtUtil.getRole(token);

                UserDTO userDTO = UserDTO.builder()
                        .username(username)
                        .role(role)
                        .build();

                CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
                Authentication authToken = new UsernamePasswordAuthenticationToken(
                        customOAuth2User,
                        null,
                        customOAuth2User.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            System.out.println("Token processing failed: " + e.getMessage());
        }
    }
}