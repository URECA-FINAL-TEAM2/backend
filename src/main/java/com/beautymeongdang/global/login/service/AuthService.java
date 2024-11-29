package com.beautymeongdang.global.login.service;

import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface AuthService {
    Map<String, Object> kakaoLogin(String code, HttpServletResponse response);
    Map<String, Object> googleLogin(String code, HttpServletResponse response);

    void logout(HttpServletResponse response, CustomOAuth2User oauth2User); //로그아웃 메서드 추가
    Map<String, Object> refreshToken(String refreshToken, HttpServletResponse response); // 토큰 갱신 메서드 추가

}