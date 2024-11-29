package com.beautymeongdang.global.login.service.Impl;

import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.login.dto.GoogleTokenResponse;
import com.beautymeongdang.global.login.dto.GoogleUserInfo;
import com.beautymeongdang.global.login.dto.KakaoTokenResponse;
import com.beautymeongdang.global.login.dto.KakaoUserInfo;
import com.beautymeongdang.global.login.repository.RefreshTokenRepository;
import com.beautymeongdang.global.login.service.AuthService;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.global.jwt.JWTUtil;
import com.beautymeongdang.global.jwt.JwtProvider;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.net.http.HttpHeaders;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Override
    public Map<String, Object> kakaoLogin(String code, HttpServletResponse response) {
        try {
            // 1. 인증 코드로 액세스 토큰 요청
            String kakaoAccessToken = getKakaoAccessToken(code);

            // 2. 액세스 토큰으로 사용자 정보 요청
            KakaoUserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);

            // 3. 사용자 정보로 회원가입/로그인 처리
            User user = userRepository.findByProviderAndProviderId("kakao", userInfo.getId())
                    .orElseGet(() -> registerKakaoUser(userInfo));

            // 4. JWT 토큰 생성
            return jwtProvider.createTokens(user, response);
        } catch (Exception e) {
            log.error("Kakao login failed: {}", e.getMessage(), e);
            throw new RuntimeException("Kakao login failed", e);
        }
    }

    @Override
    public Map<String, Object> googleLogin(String code, HttpServletResponse response) {
        try {
            // 1. 인증 코드로 액세스 토큰 요청
            String googleAccessToken = getGoogleAccessToken(code);

            // 2. 액세스 토큰으로 사용자 정보 요청
            GoogleUserInfo userInfo = getGoogleUserInfo(googleAccessToken);

            // 3. 사용자 정보로 회원가입/로그인 처리
            User user = userRepository.findByProviderAndProviderId("google", userInfo.getId())
                    .orElseGet(() -> registerGoogleUser(userInfo));

            // 4. JWT 토큰 생성
            return jwtProvider.createTokens(user, response);
        } catch (Exception e) {
            log.error("Google login failed: {}", e.getMessage(), e);
            throw new RuntimeException("Google login failed", e);
        }
    }

    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("client_secret", kakaoClientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                request,
                KakaoTokenResponse.class
        );

        return response.getBody().getAccess_token();
    }

    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                KakaoUserInfo.class
        );

        return response.getBody();
    }

    private String getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                GoogleTokenResponse.class
        );

        return response.getBody().getAccess_token();
    }

    private GoogleUserInfo getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                request,
                GoogleUserInfo.class
        );

        return response.getBody();
    }

    @Override
    public void logout(HttpServletResponse response, CustomOAuth2User oauth2User) {
        try {
            if (oauth2User != null) {
                refreshTokenRepository.deleteByUserId(oauth2User.getUserId());
            }
            removeRefreshTokenCookie(response);
            removeSessionCookie(response);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            throw new RuntimeException("Logout failed", e);
        }
    }
    @Override
    public Map<String, Object> refreshToken(String refreshToken, HttpServletResponse response) {
        try {
            if (jwtUtil.validateRefreshToken(refreshToken)) {
                String userId = jwtUtil.getUserId(refreshToken);
                User user = userRepository.findById(Long.parseLong(userId))
                        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
                return jwtProvider.createTokens(user, response);
            } else {
                throw new RuntimeException("만료된 리프레시 토큰입니다.");
            }
        } catch (EntityNotFoundException e) {
            log.error("토큰 갱신 실패: 사용자를 찾을 수 없습니다.", e);
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage(), e);
            throw new RuntimeException("토큰 갱신에 실패했습니다.", e);
        }
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 환경에서만 작동
        response.addCookie(refreshTokenCookie);
    }

    private void removeSessionCookie(HttpServletResponse response) {
        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);
    }
}