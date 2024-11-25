package com.beautymeongdang.domain.login.dto;

import com.beautymeongdang.domain.user.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//  OAuth2 인증 과정에서 사용자 정보를 담는 Spring Security의 OAuth2User 인터페이스를 구현한 클래스
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {

        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userDTO.getNickname();
    }

    public String getUsername() {

        return userDTO.getUsername();
    }
    public UserDTO getUserDTO() {
        return userDTO;
    }
}
