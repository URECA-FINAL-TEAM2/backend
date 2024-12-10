package com.beautymeongdang.global.oauth2;

import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.common.dto.ApiResponse;
import com.beautymeongdang.global.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
            User user = userRepository.findById(customUserDetails.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("accessToken", tokenInfo.get("access_token"));
            responseData.put("user", customUserDetails.getUserDTO());
            responseData.put("role", user.getRoles().toString());
            log.info("사용자 정보: {}", customUserDetails.getUserDTO());

            // 신규 사용자인 경우
            if (!customUserDetails.getUserDTO().isRegister()) {
                responseData.put("redirectUrl", "http://localhost:5173/selectRole");
                log.info("신규 사용자 - 역할 선택 페이지로 리다이렉트");
            }
            // 기존 사용자인 경우
            else {
                String role = user.getRoles().toString();
                responseData.put("role", role);

                if (role.equals("customer")) {
                    responseData.put("redirectUrl", "http://localhost:5173/customer/home");
                    log.info("기존 사용자 (고객) - 고객 홈 페이지로 리다이렉트");
                } else {
                    responseData.put("redirectUrl", "http://localhost:5173/groomer/home");
                    log.info("기존 사용자 (그루머) - 그루머 홈 페이지로 리다이렉트");
                }
            }

            ApiResponse<?> apiResponse = ApiResponse.ok(200, responseData, "로그인 성공").getBody();

            writeJsonResponse(response, apiResponse);

        } catch (Exception e) {
            ApiResponse<?> errorResponse = ApiResponse.badRequest(400, "로그인 처리 실패: " + e.getMessage()).getBody();

            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            writeJsonResponse(response, errorResponse);
        }
    }

    private void writeJsonResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}