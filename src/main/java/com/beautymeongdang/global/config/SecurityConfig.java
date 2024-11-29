package com.beautymeongdang.global.config;

import com.beautymeongdang.global.login.service.Impl.CustomOAuth2UserServiceImpl;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.jwt.JWTFilter;
import com.beautymeongdang.global.jwt.JWTUtil;
import com.beautymeongdang.global.jwt.JwtProvider;
import com.beautymeongdang.global.oauth2.CustomSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserServiceImpl customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))

                //csrf disable
                .csrf((auth) -> auth.disable())

                //Form 로그인 방식 disable
                .formLogin((auth) -> auth.disable())

                //HTTP Basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())

                //oauth2 설정 수정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                )

                //경로별 인가 작업
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/users/register/**").authenticated()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/","/oauth/**","/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/configuration/ui", "/swagger-resources/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated())

                //세션 설정 : STATELESS
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT 필터 추가
                .addFilterBefore(new JWTFilter(jwtUtil, userRepository,jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS(Cross-Origin Resource Sharing) 설정을 위한 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);

        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JWTFilter jwtFilter(JWTUtil jwtUtil, UserRepository userRepository, JwtProvider jwtProvider) {
        return new JWTFilter(jwtUtil, userRepository, jwtProvider);
    }
}