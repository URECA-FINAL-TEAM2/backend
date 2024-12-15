package com.beautymeongdang.global.chatgpt.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCompletionDto {
    // 사용할 모델
    private String model;

    // 사용할 프롬프트 명령어
    private String prompt;

    private float temperature = 0.3f;    // 낮춰서 더 집중적인 응답
    private int max_tokens = 16;         // 적절한 기본값으로 조정

    @Builder
    public CreateCompletionDto(String model, String prompt, float temperature, int max_tokens) {
        this.model = model != null ? model : "gpt-3.5-turbo";
        this.prompt = prompt;
        this.temperature = temperature > 0 ? temperature : 0.3f;
        this.max_tokens = max_tokens > 0 ? max_tokens : 50;
    }
}
