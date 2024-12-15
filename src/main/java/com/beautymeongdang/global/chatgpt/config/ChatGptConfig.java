package com.beautymeongdang.global.chatgpt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class ChatGptConfig {
    @Value("${openai.secret-key}")
    private String secretKey;

    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.debug("Authorization header set with key starting: {}",
                secretKey.substring(0, Math.min(20, secretKey.length())));

        return headers;
    }

    @Bean(name = "chatGptRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                int rawStatusCode = response.getStatusCode().value();
                if (rawStatusCode >= 400) {  // 클라이언트 또는 서버 에러
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                        String httpBodyResponse = reader.lines().collect(Collectors.joining(""));
                        log.error("Error response body: {}", httpBodyResponse);
                    }
                }
                super.handleError(response);
            }
        });
        return restTemplate;
    }
}