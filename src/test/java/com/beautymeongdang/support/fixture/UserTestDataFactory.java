package com.beautymeongdang.support.fixture;

import com.beautymeongdang.domain.user.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserTestDataFactory {
    private static final String TEST_PROVIDER_ID = "12345";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USERNAME = "testUser";

    private static final String NEW_PROVIDER_ID = "67890";
    private static final String NEW_EMAIL = "new@example.com";
    private static final String NEW_USERNAME = "newUser";

    public static User createTestUser() {
        return User.builder()
                .userName(TEST_USERNAME)
                .email(TEST_EMAIL)
                .providerId(TEST_PROVIDER_ID)
                .socialProvider("GOOGLE")
                .isRegister(true)
                .build();
    }

    public static User createNewUserWithoutRole() {
        return User.builder()
                .userName(NEW_USERNAME)
                .email(NEW_EMAIL)
                .providerId(NEW_PROVIDER_ID)
                .socialProvider("GOOGLE")
                .isRegister(false)
                .build();
    }

    /**
     * OAuth2User 객체를 생성하는 공통 메서드
     */
    private static OAuth2User createOAuth2UserInternal(String sub, String email, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", sub);
        attributes.put("email", email);
        attributes.put("name", name);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );
    }

    public static OAuth2User createOAuth2User() {
        return createOAuth2UserInternal(TEST_PROVIDER_ID, TEST_EMAIL, TEST_USERNAME);
    }

    public static OAuth2User createNewOAuth2User() {
        return createOAuth2UserInternal(NEW_PROVIDER_ID, NEW_EMAIL, NEW_USERNAME);
    }
}