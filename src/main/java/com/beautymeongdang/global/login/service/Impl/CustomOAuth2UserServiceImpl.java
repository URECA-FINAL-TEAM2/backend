package com.beautymeongdang.global.login.service.Impl;

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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements OAuth2Service {

    private final UserRepository userRepository;
    private final OAuth2AuthorizationClient oauth2Client;
    private final JwtProvider jwtProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseService oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();

        // 먼저 providerId로 사용자 찾기
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(providerId, provider);

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
        // 1. 인가 코드로 카카오 액세스 토큰 받기
        KakaoToken kakaoToken = oauth2Client.getKakaoAccessToken(code);

        // 2. 액세스 토큰으로 카카오 사용자 정보 가져오기
        KakaoUserInfo userInfo = oauth2Client.getKakaoUserInfo(kakaoToken.getAccess_token());

        // 3. 사용자 정보로 우리 서비스 사용자 찾기 또는 생성
        Optional<User> existingUser = userRepository.findByProviderIdAndSocialProvider(
                String.valueOf(userInfo.getId()),
                "KAKAO"
        );

        User user;
        if (existingUser.isEmpty()) {
            // 신규 사용자
            user = User.builder()
                    .userName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .providerId(String.valueOf(userInfo.getId()))
                    .socialProvider("KAKAO")
                    .profileImage(userInfo.getProfileImage())
                    .isRegister(false)
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get();
        }

        // 4. JWT 토큰 생성
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, null);

        // 5. UserDTO 변환 및 응답 데이터 준비
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .profileImage(user.getProfileImage())
                .isRegister(user.isRegister())
                .build();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenInfo.get("access_token"));
        responseData.put("user", userDTO);
        return responseData;
    }
}