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
    private float temperature = 0.7f;    // 더 창의적인 답변을 위해 온도 상승
    private int max_tokens = 500;        // 답변 길이 증가

    @Builder
    public CreateChatCompletionDto(List<CreateChatRequestMsgDto> messages, Float temperature, Integer max_tokens) {
        this.model = "gpt-4";            // GPT-4로 모델 변경
        this.messages = messages;
        this.temperature = temperature != null ? temperature : 0.7f;
        this.max_tokens = max_tokens != null ? max_tokens : 500;
    }
}
