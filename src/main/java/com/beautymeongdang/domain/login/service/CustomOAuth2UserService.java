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

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();        // 카카오인지 구글인지 구분하기 위함
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬, 카카오인지 구글인지 , 해당 소셜에서 제공해주는 ID 값 받기
        // OAuth2Service 부분
        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        String username = provider + " " + providerId;

        User existData = userRepository.findByUserName(username);

        if (existData == null) {
            // 새 사용자 생성
            User newUser = User.builder()
                    .userName(username)
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getName()) // name 대신 nickname 사용
                    .role("ROLE_USER")
                    .socialProvider(provider)
                    .build();

            userRepository.save(newUser);

            UserDTO userDTO = UserDTO.builder()
                    .username(username)
                    .nickname(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            return new CustomOAuth2User(userDTO);
        } else {
            // 기존 사용자 정보 업데이트
            User updatedUser = User.builder()
                    .userId(existData.getUserId())
                    .userName(existData.getUserName())
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getName())
                    .role(existData.getRole())
                    .socialProvider(existData.getSocialProvider())
                    .profileImage(existData.getProfileImage())
                    .phone(existData.getPhone())
                    .build();

            userRepository.save(updatedUser);

            UserDTO userDTO = UserDTO.builder()
                    .username(existData.getUserName())
                    .nickname(oAuth2Response.getName())
                    .role(existData.getRole())
                    .build();

            return new CustomOAuth2User(userDTO);
        }
    }
}
