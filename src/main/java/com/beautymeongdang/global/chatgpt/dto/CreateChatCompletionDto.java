package com.beautymeongdang.global.chatgpt.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateChatCompletionDto {
    private String model;
    private List<CreateChatRequestMsgDto> messages;
    private float temperature = 0.3f;    // 더 정확한 답변을 위해 온도 낮춤
    private int max_tokens = 200;        // 적절한 답변 길이 유지
    private float presence_penalty = 0.0f; // 주제 반복 방지
    private float frequency_penalty = 0.0f; // 단어 반복 방지
    private float top_p = 0.1f;          // 더 결정적인 답변을 위한 설정

    @Builder
    public CreateChatCompletionDto(List<CreateChatRequestMsgDto> messages, Float temperature, Integer max_tokens) {
        this.model = "gpt-4";
        this.messages = messages;
        this.temperature = temperature != null ? temperature : 0.3f;
        this.max_tokens = max_tokens != null ? max_tokens : 200;
    }
}