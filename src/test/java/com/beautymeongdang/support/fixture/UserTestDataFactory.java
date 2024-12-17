package com.beautymeongdang.support.fixture;

import com.beautymeongdang.domain.user.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserTestDataFactory {

    public static User createTestUser() {
        return User.builder()
                .userName("testUser")
                .email("test@example.com")
                .providerId("12345")
                .socialProvider("GOOGLE")
                .isRegister(false)
                .build();
    }

    public static OAuth2User createOAuth2User() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345");
        attributes.put("email", "test@example.com");
        attributes.put("name", "testUser");

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );
    }
}