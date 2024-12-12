package com.beautymeongdang.global.chatgpt.controller;

import com.beautymeongdang.global.chatgpt.dto.CreateChatCompletionDto;
import com.beautymeongdang.global.chatgpt.dto.CreateCompletionDto;
import com.beautymeongdang.global.chatgpt.service.ChatGptService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/gpt")
@RequiredArgsConstructor
public class ChatGptController {

    private final ChatGptService chatGptService;

    // chat gpt의 모델 리스트를 조회
    @GetMapping("/modelList")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> selectModelList() {
        try {
            List<Map<String, Object>> result = chatGptService.selectModelList();
            return ApiResponse.ok(200, result, "모델 리스트 조회 성공");
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "모델 리스트 조회 실패: " + e.getMessage());
        }
    }

    // chat gpt의 유효한 모델을 조회
    @GetMapping("/model")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isValidModel(
            @RequestParam(name = "modelName") String modelName) {
        try {
            Map<String, Object> result = chatGptService.isValidModel(modelName);
            if (result == null) {
                return ApiResponse.ok(404, null, "유효하지 않은 모델입니다.");
            }
            return ApiResponse.ok(200, result, "모델 유효성 검증 성공");
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "모델 검증 실패: " + e.getMessage());
        }
    }

    // legacy chat gpt 프롬프트 명령을 수행
    @PostMapping("/legacyPrompt")
    public ResponseEntity<ApiResponse<Map<String, Object>>> selectLegacyPrompt(
            @RequestBody CreateCompletionDto completionDto) {
        try {
            log.debug("param :: " + completionDto.toString());
            Map<String, Object> result = chatGptService.selectLegacyPrompt(completionDto);
            return ApiResponse.ok(200, result, "Legacy 프롬프트 실행 성공");
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "Legacy 프롬프트 실행 실패: " + e.getMessage());
        }
    }

    // 최신 chat gpt 프롬프트 명령을 수행
    @PostMapping("/prompt")
    public ResponseEntity<ApiResponse<Map<String, Object>>> selectPrompt(
            @RequestBody CreateChatCompletionDto completionDto) {
        try {
            log.debug("param :: " + completionDto.toString());
            Map<String, Object> result = chatGptService.selectPrompt(completionDto);
            return ApiResponse.ok(200, result, "프롬프트 실행 성공");
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "프롬프트 실행 실패: " + e.getMessage());
        }
    }
}