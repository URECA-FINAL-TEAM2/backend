package com.beautymeongdang.global.chatgpt.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateChatCompletionDto {
    private String model = "gpt-3.5-turbo";  // GPT-3 모델로 변경
    private List<CreateChatRequestMsgDto> messages;
    private float temperature = 0.3f;  // 더 일관된 응답을 위해 낮춤
    private int max_tokens = 500;      // 더 짧은 응답을 위해 크게 감소
    private float top_p = 0.1f;        // 더 집중된 응답을 위해 낮춤

    @Builder
    public CreateChatCompletionDto(List<CreateChatRequestMsgDto> messages) {
        this.messages = messages;
    }
}