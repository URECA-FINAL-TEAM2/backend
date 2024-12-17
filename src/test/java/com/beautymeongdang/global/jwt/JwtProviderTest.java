package com.beautymeongdang.global.jwt;

import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.support.fixture.UserTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;

@SpringBootTest
class JwtProviderTest {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("토큰 생성 및 검증 테스트")
    void tokenCreateAndValidateTest() {
        // given
        User testUser = UserTestDataFactory.createTestUser();
        userRepository.save(testUser);

        // when
        Map<String, Object> tokenInfo = jwtProvider.createTokens(testUser, new MockHttpServletResponse());

        // then
        assertThat(tokenInfo).containsKeys("access_token", "encrypted_access_token");
        String accessToken = (String) tokenInfo.get("access_token");
        String encryptedToken = (String) tokenInfo.get("encrypted_access_token");
        assertThat(jwtProvider.validateToken(encryptedToken)).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 테스트")
    void expiredTokenTest() throws Exception {
        // given
        User testUser = UserTestDataFactory.createTestUser();
        userRepository.save(testUser);

        // when
        Map<String, Object> tokenInfo = jwtProvider.createTokens(testUser, new MockHttpServletResponse());
        Thread.sleep(1000); // 토큰 만료 대기

        // then
        String accessToken = (String) tokenInfo.get("access_token");
        String encryptedToken = (String) tokenInfo.get("encrypted_access_token");
        assertThat(jwtProvider.validateToken(encryptedToken)).isFalse();
    }
}