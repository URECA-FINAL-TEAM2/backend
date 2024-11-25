package com.beautymeongdang.domain.login.service;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.domain.login.dto.GoogleResponse;
import com.beautymeongdang.domain.login.dto.KakaoResponse;
import com.beautymeongdang.domain.login.dto.OAuth2Response;
import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null; // 지원하지 않는 제공자 처리
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        String username = provider + " " + providerId;

        Optional<User> existingUser = userRepository.findByUserName(username);

        User user;
        if (existingUser.isEmpty()) {
            // 새 사용자 생성
            user = User.builder()
                    .userName(username)
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getName())
                    .socialProvider(provider)
                    .profileImage(oAuth2Response.getProfileImage())
                    .role("ROLE_USER") // 새로운 사용자의 역할 설정
                    .build();
            userRepository.save(user);
        } else {
            user = existingUser.get(); // 기존 사용자 가져오기
            // 필요하다면 OAuth 응답에서 가져온 정보로 기존 사용자 정보를 업데이트합니다.  기존 데이터를 덮어쓰는 것은 주의해야 합니다.
        }

        UserDTO userDTO = UserDTO.builder()
                .username(user.getUserName())
                .nickname(user.getNickname())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .build();

        return new CustomOAuth2User(userDTO);
    }
}