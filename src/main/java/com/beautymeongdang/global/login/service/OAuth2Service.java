package com.beautymeongdang.global.login.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public interface  OAuth2Service {
    OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException;
    Map<String, Object> processKakaoLogin(String code, HttpServletResponse response);
    Map<String, Object> processGoogleLogin(String code, HttpServletResponse response);
}
