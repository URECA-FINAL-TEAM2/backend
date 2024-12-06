package com.beautymeongdang.global.oauth2;

import com.beautymeongdang.global.login.entity.GoogleToken;
import com.beautymeongdang.global.login.entity.GoogleUserInfo;
import com.beautymeongdang.global.login.entity.KakaoToken;
import com.beautymeongdang.global.login.entity.KakaoUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthorizationClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    public KakaoToken getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // 요청 파라미터 로깅
        log.info("login-log Kakao token request parameters - clientId: {}, clientSecret: {} , redirectUri: {}", clientId, clientSecret, redirectUri);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoToken> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    KakaoToken.class
            );
            log.info("login-log ✅ 카카오 토큰 발급 성공");
            return response.getBody();
        } catch (Exception e) {
            log.error("login-log 카카오 토큰 요청 실패", e);
            throw new RuntimeException("카카오 토큰 발급 실패", e);
        }
    }

    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        log.info("login-log 👤 카카오 사용자 정보 요청 시작");
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            log.info("login-log📄 카카오 응답 데이터: {}", response.getBody());
            return KakaoUserInfo.builder()
                    .id(jsonNode.get("id").asLong())
                    .email(jsonNode.get("kakao_account").get("email").asText())
                    .name(jsonNode.get("properties").get("nickname").asText())
                    .build();
        } catch (Exception e) {
            log.error("login-log 카카오 사용자 정보 요청 실패", e);
            throw new RuntimeException("login-log 카카오 사용자 정보 조회 실패", e);
        }
    }
    public GoogleToken getGoogleAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        log.info("login-log Google token request parameters - clientId: {}, redirectUri: {}",
                googleClientId, googleRedirectUri);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<GoogleToken> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    GoogleToken.class
            );
            log.info("login-log ✅ 구글 토큰 발급 성공");
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("login-log 구글 토큰 요청 실패. Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("구글 토큰 발급 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("login-log 구글 토큰 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("구글 토큰 발급 실패", e);
        }
    }

    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
        log.info("login-log 👤 구글 사용자 정보 요청 시작");
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            String responseBody = response.getBody();
            log.info("login-log📄 응답 코드: {}", response.getStatusCode());
            log.info("login-log📄 응답 헤더: {}", response.getHeaders());
            log.info("login-log📄 구글 응답 데이터: {}", responseBody);

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            log.info("login-log📄 JSON 파싱 결과: {}", jsonNode);

            // 각 필드 존재 여부 확인
            log.info("login-log📄 필드 체크 - sub: {}, email: {}, name: {}, picture: {}, email_verified: {}, locale: {}",
                    jsonNode.has("sub"), jsonNode.has("email"), jsonNode.has("name"),
                    jsonNode.has("picture"), jsonNode.has("email_verified"), jsonNode.has("locale"));

            return GoogleUserInfo.builder()
                    .id(jsonNode.get("sub").asText())
                    .email(jsonNode.get("email").asText())
                    .name(jsonNode.get("name").asText())
                    .profileImage(jsonNode.get("picture").asText())
                    .emailVerified(jsonNode.get("email_verified").asBoolean())
                    .locale(jsonNode.get("locale").asText())
                    .build();
        } catch (Exception e) {
            log.error("login-log 구글 사용자 정보 요청 실패 - 에러 타입: {}, 메시지: {}",
                    e.getClass().getName(), e.getMessage(), e);
            throw new RuntimeException("구글 사용자 정보 조회 실패: " + e.getMessage(), e);
        }
    }
}