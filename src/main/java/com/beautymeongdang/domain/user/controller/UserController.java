package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.global.common.dto.ApiResponse;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerCustomer(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @RequestBody CustomerRegisterRequestDTO customerDTO,
            HttpServletResponse response) {
        try {
            User user = userService.registerCustomer(oauth2User.getUserId(), customerDTO);
            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);
            return ApiResponse.ok(201, tokenInfo, "고객 회원가입 성공");
        } catch (Exception e) {
            log.error("고객 회원가입 실패: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "고객 회원가입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/register/groomer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerGroomer(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @RequestBody GroomerRegisterRequestDTO registrationDTO,
            HttpServletResponse response) {
        try {
            User user = userService.registerGroomer(oauth2User.getUserId(), registrationDTO);
            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);
            return ApiResponse.ok(201, tokenInfo, "미용사 회원가입 성공");
        } catch (Exception e) {
            log.error("미용사 회원가입 실패: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "미용사 회원가입 실패: " + e.getMessage());
        }
    }
}