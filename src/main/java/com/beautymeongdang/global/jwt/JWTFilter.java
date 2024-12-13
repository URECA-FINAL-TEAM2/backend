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
            String requestURI = request.getRequestURI();
            log.debug("Processing request: {}", requestURI);

            String accessToken = request.getHeader("Authorization");
            log.debug("Received Authorization header: {}", accessToken);

            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
                log.debug("Extracted token: {}", accessToken);
                processAccessToken(accessToken);
            }
            // 쿼리 파라미터에서 토큰 추출
            else if (request.getParameter("token") != null) {
                accessToken = request.getParameter("token");
                log.debug("Extracted token from query parameter: {}", accessToken);
                processAccessToken(accessToken);

                // 3. Refresh Token 처리
            }
            else {
                log.debug("No access token found, checking refresh token");
                processRefreshToken(request, response);
            }

            // 현재 인증 상태 로깅
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("Current authentication: {}", authentication);

        } catch (Exception e) {
            log.error("Error processing JWT token", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private void processAccessToken(String token) {
        try {
            log.debug("Processing access token");

            if (jwtUtil.isExpired(token)) {
                log.debug("Token is expired");
                return;
            }

            String userId = jwtUtil.getUserId(token);
            log.debug("Extracted userId from token: {}", userId);

            if (userId == null) {
                log.debug("userId is null");
                return;
            }

            User user = userRepository.findByIdWithRoles(Long.parseLong(userId))
                    .orElse(null);

            if (user == null) {
                log.debug("User not found for userId: {}", userId);
                return;
            }

            log.debug("Found user: {}", user.getUserId());
            processTokenWithUser(user);

        } catch (Exception e) {
            log.error("Failed to process access token", e);
        }
    }

    private void processRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                log.debug("No cookies found");
                return;
            }

            String refreshToken = findRefreshToken(cookies);
            if (refreshToken == null) {
                log.debug("No refresh token found");
                return;
            }

            if (jwtUtil.isExpired(refreshToken)) {
                log.debug("Refresh token is expired");
                return;
            }

            String userId = jwtUtil.getUserId(refreshToken);
            if (userId == null) {
                log.debug("No userId in refresh token");
                return;
            }

            User user = userRepository.findByIdWithRoles(Long.parseLong(userId))
                    .orElse(null);

            if (user == null) {
                log.debug("User not found for refresh token");
                return;
            }

            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);
            log.debug("Created new tokens for user: {}", user.getUserId());
            processTokenWithUser(user);

        } catch (Exception e) {
            log.error("Failed to process refresh token", e);
        }
    }

    private void processTokenWithUser(User user) {
        try {
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
            log.debug("Successfully set authentication for user: {}", user.getUserId());

        } catch (Exception e) {
            log.error("Failed to process user authentication", e);
        }
    }

    private String findRefreshToken(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldNotFilter = path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/login/oauth2/") ||
                path.startsWith("/wp-admin/") ||
                path.startsWith("/wordpress/") ||
                path.equals("/") ||
                (path.contains(".") && !path.endsWith(".html"));

        if (shouldNotFilter) {
            log.debug("Skipping JWT filter for path: {}", path);
        }

        return shouldNotFilter;
    }
}