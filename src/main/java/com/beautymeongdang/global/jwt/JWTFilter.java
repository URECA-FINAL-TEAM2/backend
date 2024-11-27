package com.beautymeongdang.global.jwt;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.Role;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.domain.user.service.UserService;
import io.jsonwebtoken.Jwts;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            processToken(accessToken);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String refreshToken = findRefreshToken(cookies);
                if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
                    String userId = jwtUtil.getUserId(refreshToken);
                    if (userId != null) {
                        User user = userRepository.findByIdWithRoles(Long.parseLong(userId))
                                .orElse(null);

                        if (user != null) {
                            Set<String> roles = user.getUserRoles().stream()
                                    .map(userRole -> userRole.getRole().getName())
                                    .collect(Collectors.toSet());

                            String newAccessToken = jwtUtil.createAccessToken(
                                    userId,
                                    user.getNickname(),
                                    roles,
                                    1000 * 60 * 30L // 30분
                            );
                            response.setHeader("Authorization", "Bearer " + newAccessToken);
                            processTokenWithUser(user);
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String findRefreshToken(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private void processToken(String token) {
        try {
            if (!jwtUtil.isExpired(token)) {
                String userId = jwtUtil.getUserId(token);
                if (userId != null) {
                    User user = userRepository.findById(Long.parseLong(userId))
                            .orElse(null);
                    if (user != null) {
                        processTokenWithUser(user);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Token processing failed: ", e);
        }
    }

    private void processTokenWithUser(User user) {
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .nickname(user.getNickname())
                .roles(user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
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