package com.beautymeongdang.global.oauth2;

import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;

@Component
@AllArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        User user = userRepository.findById(customUserDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // JWT 토큰 생성
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);

        // 신규 사용자인 경우
        if (!customUserDetails.getUserDTO().isRegister()) {

            // 토큰 정보를 세션에 저장
            request.getSession().setAttribute("tokenInfo", tokenInfo);

            //추가 정보 입력 페이지로 리다이렉트
            response.sendRedirect("/index1.html");

            // 기존 사용자인 경우
        } else {

            // Access Token을 JSON 응답으로 전송
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", customUserDetails.getUserId());
            responseData.put("nickname", customUserDetails.getName());
            responseData.putAll(tokenInfo);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseData));

            // index.html로 리다이렉트
            response.sendRedirect("/index.html");
        }
    }
}