package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.common.dto.ApiResponse;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(HttpServletRequest request,
                                                                   HttpServletResponse response) {
        try {
            userService.logout(request, response);
            Map<String, String> result = new HashMap<>();
            result.put("result", "JWT Token delete");

            return ApiResponse.ok(201, result, "logout Success");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "Logout failed: " + e.getMessage());
        }
    }


    // Controller
    @GetMapping("/nickname/{nickname}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkNicknameAvailability(@PathVariable String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        String message = userService.getNicknameCheckMessage(nickname);

        if (!isAvailable) {
            return ApiResponse.badRequest(400, message);  // 이미 사용 중인 경우
        }

        return ApiResponse.ok(200, true, message);  // 사용 가능한 경우
    }


    // 테스트 용 (삭제 가능)
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestParam Long userId, HttpServletResponse response) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // JWT 토큰 생성
            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);

            // 응답 데이터 구성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("isRegister", user.isRegister());
            responseData.put("accessToken", tokenInfo.get("accessToken"));

            // 사용자 정보 구성
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("nickname", user.getNickname());
            responseData.put("userInfo", userInfo);

            return ApiResponse.ok(200, responseData, "로그인 성공");
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage(), e);
            return ApiResponse.badRequest(400, "로그인 실패: " + e.getMessage());
        }
    }
}