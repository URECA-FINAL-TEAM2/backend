package com.beautymeongdang.global.chatgpt.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCompletionDto {
    // 사용할 모델
    private String model;

    // 사용할 프롬프트 명령어
    private String prompt;

    // 프롬프트의 다양성을 조절할 명령어(default : 1)
    private float temperature = 1;

    // 최대 사용할 토큰(default : 16)
    private int max_tokens = 16;

    @Builder
    public CreateCompletionDto(String model, String prompt, float temperature, int max_tokens) {
        this.model = model;
        this.prompt = prompt;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }
}