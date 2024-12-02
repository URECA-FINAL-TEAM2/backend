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
            ApiResponse<?> apiResponse;

            // 신규 사용자인 경우
            if (!customUserDetails.getUserDTO().isRegister()) {
                request.getSession().setAttribute("tokenInfo", tokenInfo);

                responseData.put("isRegister", false);
                responseData.put("message", "추가 정보 입력이 필요합니다.");
                apiResponse = ApiResponse.ok(200, responseData, "신규 사용자 로그인 성공").getBody();

                writeJsonResponse(response, apiResponse);
                response.sendRedirect("/InfoRequired.jsx");
            }
            // 기존 사용자인 경우
            else {
                responseData.put("isRegister", true);
                responseData.put("userId", customUserDetails.getUserId());
                responseData.put("nickname", customUserDetails.getName());
                responseData.putAll(tokenInfo);

                apiResponse = ApiResponse.ok(200, responseData, "로그인 성공").getBody();

                writeJsonResponse(response, apiResponse);
                response.sendRedirect("/Login.jsx");
            }
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            handleAuthenticationError(response, e);
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        ApiResponse<?> errorResponse = ApiResponse.badRequest(400, "로그인 처리 실패: " + e.getMessage()).getBody();
        writeJsonResponse(response, errorResponse);

        // 에러 페이지로 리다이렉트 (선택사항)
        response.sendRedirect("/error.html");
    }

    private void writeJsonResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}