package com.beautymeongdang.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",      // 로컬 개발 환경
                        "https://beautymeongdang.com" // 프로덕션 환경
                )
                .allowedMethods("*")              // 모든 HTTP 메서드 허용
                .allowedHeaders("*")              // 모든 헤더 허용
                .exposedHeaders("Set-Cookie")     // 쿠키 헤더 노출
                .allowCredentials(true);          // 인증 정보(쿠키, 인증 헤더 등) 허용
    }
}