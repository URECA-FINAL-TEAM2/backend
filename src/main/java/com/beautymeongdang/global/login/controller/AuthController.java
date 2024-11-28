package com.beautymeongdang.global.login.controller;

import com.beautymeongdang.global.login.service.AuthService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kakaoLogin(
            @RequestBody Map<String, String> request,
            HttpServletResponse response
    ) {
        try {
            String code = request.get("code");
            Map<String, Object> tokenInfo = authService.kakaoLogin(code, response);
            return ApiResponse.ok(200, tokenInfo, "로그인 성공");
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "로그인 실패: " + e.getMessage());
        }
    }
    @PostMapping("/google/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> googleLogin(
            @RequestBody Map<String, String> request,
            HttpServletResponse response
    ) {
        try {
            String code = request.get("code");
            Map<String, Object> tokenInfo = authService.googleLogin(code, response);
            return ApiResponse.ok(200, tokenInfo, "로그인 성공");
        } catch (Exception e) {
            log.error("구글 로그인 실패: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "구글 로그인 실패: " + e.getMessage());
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            HttpServletResponse response
    ) {
        try {
            authService.logout(response, oauth2User);
            return ApiResponse.ok(200, null, "로그아웃 성공");
        } catch (Exception e) {
            log.error("로그아웃 실패: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "로그아웃 실패: " + e.getMessage());
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        try {
            if (refreshToken == null) {
                return ApiResponse.badRequest(401, "Refresh token is missing");
            }
            Map<String, Object> tokenInfo = authService.refreshToken(refreshToken, response);
            return ApiResponse.ok(200, tokenInfo, "토큰 갱신 성공");
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage(), e);
            return ApiResponse.badRequest(401, "토큰 갱신 실패: " + e.getMessage());
        }
    }
}

