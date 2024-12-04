package com.beautymeongdang.global.login.service.impl;

import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.global.jwt.JwtProvider;
import com.beautymeongdang.global.login.entity.KakaoToken;
import com.beautymeongdang.global.login.entity.KakaoUserInfo;
import com.beautymeongdang.global.login.service.OAuth2Service;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import com.beautymeongdang.global.oauth2.GoogleResponse;
import com.beautymeongdang.global.oauth2.KakaoResponse;
import com.beautymeongdang.global.login.service.OAuth2ResponseService;

import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.oauth2.OAuth2AuthorizationClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


// CustomOAuth2UserServiceImpl.java

@Slf4j
@Service
@AllArgsConstructor
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements OAuth2Service {

    private final UserRepository userRepository;
    private final OAuth2AuthorizationClient oauth2Client;
    private final JwtProvider jwtProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("🔑 OAuth2 로그인 시작 - Provider: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("👤 OAuth2 유저 정보 로드 완료: {}", oAuth2User.getAttributes());
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseService oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            log.info("🟡 카카오 응답 처리 중...");
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();

        // 먼저 providerId로 사용자 찾기
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(providerId, provider);
        log.info("🔍 기존 유저 조회 결과: {}", existingUser.isPresent() ? "유저 존재" : "신규 유저");

        User user;
        if (existingUser.isEmpty()) {
            // 신규 사용자
            user = User.builder()
                    .userName(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .providerId(providerId)
                    .socialProvider(provider)
                    .profileImage(oAuth2Response.getProfileImage())
                    .isRegister(false)
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get();
        }

        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .profileImage(user.getProfileImage())
                .isRegister(user.isRegister())
                .build();

        return new CustomOAuth2User(userDTO);
    }


    @Override
    public Map<String, Object> processKakaoLogin(String code) {
        log.info("🟡 카카오 로그인 프로세스 시작 - 인가 코드: {}", code);
        // 1. 프론트엔드에서 받은 인가 코드로 카카오 액세스 토큰을 요청하고 받아옴
        KakaoToken kakaoToken = oauth2Client.getKakaoAccessToken(code);
        log.info("🎫 카카오 액세스 토큰 발급 완료");

        // 2. 받아온 액세스 토큰으로 카카오 API를 호출하여 사용자 정보(이름, 이메일 등)를 조회
        KakaoUserInfo userInfo = oauth2Client.getKakaoUserInfo(kakaoToken.getAccess_token());
        log.info("👤 카카오 유저 정보 조회 완료 - ID: {}, Email: {}", userInfo.getId(), userInfo.getEmail());

        // 3. 카카오에서 받은 고유 ID와 제공자 정보(KAKAO)로 기존 사용자가 있는지 DB에서 조회
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(
                String.valueOf(userInfo.getId()),
                "KAKAO"
        );

        User user;
        if (existingUser.isEmpty()) {
            // 기존 사용자가 없으면 새로운 사용자 객체를 생성
            // isRegister(false)로 설정하여 추가 정보 입력이 필요함을 표시
            user = User.builder()
                    .userName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .providerId(String.valueOf(userInfo.getId()))
                    .socialProvider("KAKAO")
                    .profileImage(userInfo.getProfileImage())
                    .isRegister(false)
                    .build();
            userRepository.save(user); // 새로운 사용자 정보를 DB에 저장
        } else {
            // 기존 사용자가 있으면 해당 사용자 정보를 가져옴
            user = existingUser.get();
        }

        // 4. 사용자 인증을 위한 JWT 토큰을 생성 (접근 토큰, 리프레시 토큰 등)
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, null);

        // 5. 클라이언트에 전달할 사용자 정보를 DTO 객체로 변환
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .profileImage(user.getProfileImage())
                .isRegister(user.isRegister())
                .build();

        // 6. 클라이언트에 반환할 응답 데이터를 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenInfo.get("access_token")); // JWT 접근 토큰
        responseData.put("user", userDTO);                              // 사용자 정보
        responseData.put("role", user.getRoles().iterator().next().toString()); // 사용자 권한
        responseData.put("isNewUser", !user.isRegister());             // 신규 사용자 여부

        // 7. 최종 응답 데이터 반환
        return responseData;
    }
}
