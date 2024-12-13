package com.beautymeongdang.global.config;

import com.beautymeongdang.global.login.service.impl.CustomOAuth2UserServiceImpl;
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
imp팅
                        ).permitAll()
                        // API 및 Swagger 관련 경로
                        .requestMatchers(
                                "/api/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/configuration/ui",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )


                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT 필터 추가 (한 번만 추가)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 오리진 설정
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",    // 프론트엔드 로컬
                "http://localhost:8081",    // 백엔드 로컬
                "https://beautymeongdang.com",
                "https://www.beautymeongdang.com",
                "https://beautymeongdang.vercel.app"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Refresh-Token",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Access-Token",
                "Access-Control-Expose-Headers"
        ));

        // 자격증명 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
