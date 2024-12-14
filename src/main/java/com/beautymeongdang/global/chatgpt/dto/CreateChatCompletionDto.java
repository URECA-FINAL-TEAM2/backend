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
    private float temperature = 0.3f;    // 추가
    private int max_tokens = 50;         // 추가

    @Builder
    public CreateChatCompletionDto(List<CreateChatRequestMsgDto> messages, Float temperature, Integer max_tokens) {
        this.model = "gpt-3.5-turbo";
        this.messages = messages;
        this.temperature = temperature != null ? temperature : 0.3f;
        this.max_tokens = max_tokens != null ? max_tokens : 50;
    }
}
