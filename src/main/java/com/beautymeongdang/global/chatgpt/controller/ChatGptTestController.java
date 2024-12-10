package com.beautymeongdang.global.chatgpt.controller;

import com.beautymeongdang.global.chatgpt.dto.ChatCompletionDto;
import com.beautymeongdang.global.chatgpt.dto.ChatRequestMsgDto;
import com.beautymeongdang.global.chatgpt.dto.CompletionDto;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gpt/test")
@RequiredArgsConstructor
public class ChatGptTestController {

    @GetMapping("/completion")
    public ResponseEntity<ApiResponse<CompletionDto>> getCompletionExample() {
        CompletionDto completionDto = CompletionDto.builder()
                .model("gpt-4-mini")
                .prompt("애견 미용실 운영에 대한 조언을 해주세요")
                .temperature(0.7f)
                .max_tokens(1000)
                .build();

        return ApiResponse.ok(200, completionDto, "Legacy Completion 예시");
    }

    @GetMapping("/chat-completion")
    public ResponseEntity<ApiResponse<ChatCompletionDto>> getChatCompletionExample() {
        ChatRequestMsgDto message = ChatRequestMsgDto.builder()
                .role("user")
                .content("애견 미용실 운영에 대한 조언을 해주세요")
                .build();

        ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                .messages(Arrays.asList(message))
                .build();

        return ApiResponse.ok(200, chatCompletionDto, "Chat Completion 예시");
    }

    @GetMapping("/multi-chat")
    public ResponseEntity<ApiResponse<ChatCompletionDto>> getMultiChatExample() {
        List<ChatRequestMsgDto> messages = Arrays.asList(
                ChatRequestMsgDto.builder()
                        .role("system")
                        .content("당신은 10년 경력의 애견 미용 전문가입니다.")
                        .build(),
                ChatRequestMsgDto.builder()
                        .role("user")
                        .content("소형견 미용 시 주의할 점은 무엇인가요?")
                        .build(),
                ChatRequestMsgDto.builder()
                        .role("assistant")
                        .content("소형견은 특히 스트레스에 민감하므로 차분한 환경을 조성해야 합니다.")
                        .build(),
                ChatRequestMsgDto.builder()
                        .role("user")
                        .content("구체적인 예시를 들어주세요.")
                        .build()
        );

        ChatCompletionDto chatCompletionDto = ChatCompletionDto.builder()
                .messages(messages)
                .build();

        return ApiResponse.ok(200, chatCompletionDto, "다중 메시지 Chat Completion 예시");
    }

    @GetMapping("/request-format")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRequestFormat() {
        Map<String, Object> formats = new HashMap<>();

        formats.put("completion_format", CompletionDto.builder()
                .model("gpt-4-mini")
                .prompt("your_prompt_here")
                .temperature(0.7f)
                .max_tokens(1000)
                .build());

        formats.put("chat_format", ChatCompletionDto.builder()
                .messages(Arrays.asList(
                        ChatRequestMsgDto.builder()
                                .role("user")
                                .content("your_message_here")
                                .build()
                ))
                .build());

        return ApiResponse.ok(200, formats, "요청 형식 예시");
    }
}