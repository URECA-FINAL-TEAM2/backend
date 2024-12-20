package com.beautymeongdang.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("https://beautymeongdang.com").description("Production Server"),
                        new Server().url("http://localhost:8081").description("Local Server")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("미용멍당 API")
                .description("미용멍당 REST API 명세 및 테스트")
                .version("v1.0");
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

}
