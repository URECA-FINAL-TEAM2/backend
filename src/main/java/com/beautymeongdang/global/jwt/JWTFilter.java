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

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

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
                            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);
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
                    User user = userRepository.findByIdWithRoles(Long.parseLong(userId))
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
                .build();

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customOAuth2User,
                null,
                customOAuth2User.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/login/oauth2/") ||
                path.startsWith("/wp-admin/") ||  // 워드프레스 스캐닝 차단
                path.startsWith("/wordpress/") ||  // 워드프레스 스캐닝 차단
                path.equals("/") ||
                (path.contains(".") && !path.endsWith(".html")); // 정적 리소스 제외
    }
}