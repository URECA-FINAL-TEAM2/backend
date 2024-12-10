package com.beautymeongdang.global.chatgpt.controller;

import com.beautymeongdang.global.chatgpt.dto.ChatCompletionDto;
import com.beautymeongdang.global.chatgpt.dto.ChatRequestMsgDto;
import com.beautymeongdang.global.chatgpt.dto.CompletionDto;
import com.beautymeongdang.global.chatgpt.service.ChatGptService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gpt")
@RequiredArgsConstructor
public class ChatGptTestController {
    private final ChatGptService chatGptService;  // 서비스 주입

    @PostMapping("/completion")
    @Operation(summary = "단일 프롬프트 테스트", description = "ChatGPT API의 단일 프롬프트 요청을 테스트합니다")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testCompletion(
            @RequestBody CompletionDto requestDto
    ) {
        Map<String, Object> response = chatGptService.selectLegacyPrompt(requestDto);
        return ApiResponse.ok(200, response, "Completion 테스트 성공");
    }

    @PostMapping("/chat-completion")
    @Operation(summary = "채팅 프롬프트 테스트", description = "ChatGPT API의 채팅 방식 요청을 테스트합니다")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testChatCompletion(
            @RequestBody List<ChatRequestMsgDto> messages
    ) {
        ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                .messages(messages)
                .build();

        Map<String, Object> response = chatGptService.selectPrompt(chatCompletionDto);
        return ApiResponse.ok(200, response, "Chat Completion");
    }
}