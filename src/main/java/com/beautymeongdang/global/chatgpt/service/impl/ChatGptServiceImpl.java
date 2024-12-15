package com.beautymeongdang.global.chatgpt.service.impl;

import com.beautymeongdang.global.chatgpt.config.ChatGptConfig;
import com.beautymeongdang.global.chatgpt.dto.CreateChatCompletionDto;
import com.beautymeongdang.global.chatgpt.dto.CreateCompletionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.beautymeongdang.global.chatgpt.service.ChatGptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGptServiceImpl implements ChatGptService {

    private final ChatGptConfig chatGptConfig;

    @Value("${openai.url.model}")
    private String modelUrl;

    @Value("${openai.url.model-list}")
    private String modelListUrl;

    @Value("${openai.url.prompt}")
    private String promptUrl;

    @Value("${openai.url.legacy-prompt}")
    private String legacyPromptUrl;

    @Autowired
    @Qualifier("chatGptRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public List<Map<String, Object>> selectModelList() {
        log.debug("[+] 모델 리스트를 조회합니다.");

        try {
            HttpHeaders headers = chatGptConfig.createHeaders();
            log.debug("Authorization Header: {}", headers.get(HttpHeaders.AUTHORIZATION));

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    modelUrl,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            ObjectMapper om = new ObjectMapper();
            Map<String, Object> data = om.readValue(response.getBody(), new TypeReference<>() {});

            List<Map<String, Object>> resultList = (List<Map<String, Object>>) data.get("data");
            resultList.forEach(object -> {
                log.debug("ID: {}, Object: {}, Created: {}, Owned By: {}",
                        object.get("id"), object.get("object"),
                        object.get("created"), object.get("owned_by"));
            });

            return resultList;
        } catch (Exception e) {
            log.error("모델 리스트 조회 실패", e);
            return List.of();
        }
    }

    @Override
    public Map<String, Object> isValidModel(String modelName) {
        log.debug("[+] 모델 유효성 검증: {}", modelName);

        try {
            HttpHeaders headers = chatGptConfig.createHeaders();
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    modelListUrl + "/" + modelName,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            ObjectMapper om = new ObjectMapper();
            return om.readValue(response.getBody(), new TypeReference<>() {});
        } catch (Exception e) {
            log.error("모델 유효성 검증 실패: {}", modelName, e);
            return Map.of("error", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> selectLegacyPrompt(CreateCompletionDto completionDto) {
        log.debug("[+] 레거시 프롬프트 실행");

        try {
            HttpHeaders headers = chatGptConfig.createHeaders();
            log.debug("Request Headers: {}", headers);
            log.debug("Request Body: {}", completionDto);

            HttpEntity<CreateCompletionDto> requestEntity = new HttpEntity<>(completionDto, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    legacyPromptUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.debug("Response Status: {}", response.getStatusCode());
            log.debug("Response Body: {}", response.getBody());

            ObjectMapper om = new ObjectMapper();
            return om.readValue(response.getBody(), new TypeReference<>() {});
        } catch (Exception e) {
            log.error("레거시 프롬프트 실행 실패", e);
            return Map.of("error", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> selectPrompt(CreateChatCompletionDto chatCompletionDto) {
        log.debug("[+] 신규 프롬프트 실행");

        try {
            HttpHeaders headers = chatGptConfig.createHeaders();
            log.debug("Request Headers: {}", headers);
            log.debug("Request Body: {}", chatCompletionDto);

            HttpEntity<CreateChatCompletionDto> requestEntity = new HttpEntity<>(chatCompletionDto, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    promptUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.debug("Response Status: {}", response.getStatusCode());
            log.debug("Response Body: {}", response.getBody());

            ObjectMapper om = new ObjectMapper();
            return om.readValue(response.getBody(), new TypeReference<>() {});
        } catch (Exception e) {
            log.error("신규 프롬프트 실행 실패", e);
            return Map.of("error", e.getMessage());
        }
    }
}