package com.beautymeongdang.global.oauth2;

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
            ResponseEntity<String> response = restTemplate.postForEntity(
                    tokenUrl,
                    request,
                    String.class  // KakaoToken.class 대신 String.class로 변경
            );
            log.info("login-log 카카오 토큰 응답: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.getBody(), KakaoToken.class);
            } else {
                log.error("login-log 카카오 토큰 요청 실패 - 상태 코드: {}, 응답: {}",
                        response.getStatusCode(),
                        response.getBody());
                throw new RuntimeException("카카오 토큰 발급 실패: " + response.getBody());
            }
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
}