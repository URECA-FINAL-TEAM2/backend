package com.beautymeongdang.domain.login.service;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.domain.login.dto.GoogleResponse;
import com.beautymeongdang.domain.login.dto.KakaoResponse;
import com.beautymeongdang.domain.login.dto.OAuth2Response;
import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.Role;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.entity.UserRole;
import com.beautymeongdang.domain.user.repository.RoleRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // RoleRepository 추가

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
            return null;
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        String username = provider + " " + providerId;

        Optional<User> existingUser = userRepository.findByUserName(username);

        User user;
        if (existingUser.isEmpty()) {
            user = User.builder()
                    .userName(username)
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getName())
                    .socialProvider(provider)
                    .profileImage(oAuth2Response.getProfileImage())
                    .build();

            // RoleRepository를 사용하여 Role 가져오기
            Role customerRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new EntityNotFoundException("CUSTOMER role not found"));
            user.addRole(customerRole); // User 엔티티에서 직접 addRole 메서드 호출

            userRepository.save(user);
        } else {
            user = existingUser.get();
        }

        UserDTO userDTO = UserDTO.builder()
                .username(user.getUserName())
                .nickname(user.getNickname())
                .roles(user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet()))
                .profileImage(user.getProfileImage())
                .build();

        return new CustomOAuth2User(userDTO);
    }
}