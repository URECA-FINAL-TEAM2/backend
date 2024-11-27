package com.beautymeongdang.domain.login.dto;

import com.beautymeongdang.domain.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

//  OAuth2 인증 과정에서 사용자 정보를 담는 Spring Security의 OAuth2User 인터페이스를 구현한 클래스
@Getter
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        if (userDTO.getRoles() != null) {
            collection.add(() -> userDTO.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()).toString());
        } else {
            collection.add(() -> new ArrayList<SimpleGrantedAuthority>().toString());
        }

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

    public Long getUserId() {
        return userDTO.getId();
    }
}