package com.beautymeongdang.global.login.service.Impl;

import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.global.oauth2.CustomOAuth2User;
import com.beautymeongdang.global.oauth2.GoogleResponse;
import com.beautymeongdang.global.oauth2.KakaoResponse;
import com.beautymeongdang.global.login.service.OAuth2ResponseService;

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
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

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
        String username = provider + "_" + providerId; // "_"로 변경

        Optional<User> existingUser = userRepository.findByUserName(username);

        User user;
        if (existingUser.isEmpty()) {
            user = User.builder()
                    .userName(oAuth2Response.getName())     // 실제 이름을 userName에 저장
                    .email(oAuth2Response.getEmail())
                    .nickname(username)                     // provider_id를 nickname에 저장
                    .socialProvider(provider)
                    .profileImage(oAuth2Response.getProfileImage())
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
                .build();

        return new CustomOAuth2User(userDTO);
    }
}