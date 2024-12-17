package com.beautymeongdang.support.config;

import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.jwt.JWTUtil;
import com.beautymeongdang.global.jwt.JwtProvider;
import com.beautymeongdang.global.login.service.impl.CustomOAuth2UserServiceImpl;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.oauth2.OAuth2AuthorizationClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@Import({OAuth2AuthorizationClient.class, JWTUtil.class})
public class TestSecurityConfig {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OAuth2AuthorizationClient oauth2Client;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private GroomerRepository groomerRepository;

    @MockBean
    private JwtProvider jwtProvider;

    @Bean
    public CustomOAuth2UserServiceImpl customOAuth2UserService() {
        // Mock으로 생성된 의존성들을 주입
        return Mockito.mock(CustomOAuth2UserServiceImpl.class);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService())
                        )
                );

        return http.build();
    }
}