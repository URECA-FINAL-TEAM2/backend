package com.beautymeongdang.global.chatgpt.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateChatCompletionDto {
    private String model = "gpt-4";
    private List<CreateChatRequestMsgDto> messages;
    private float temperature = 0.2f;
    private int max_tokens = 300;
    private float top_p = 0.1f;

    @Builder
    public CreateChatCompletionDto(List<CreateChatRequestMsgDto> messages) {
        this.messages = messages;
    }
}