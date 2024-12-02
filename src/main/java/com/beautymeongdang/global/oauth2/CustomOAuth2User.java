package com.beautymeongdang.global.oauth2;

import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (userDTO.getRoles() != null) {
            for (Role role : userDTO.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.name()));
            }
        }
        return authorities;
    }

    @Override
    public String getName() {
        // providerId를 principalName으로 사용
        return userDTO.getUsername(); // 또는 userDTO에서 적절한 식별자를 반환
    }

    public Long getUserId() {
        return userDTO.getId();
    }
}