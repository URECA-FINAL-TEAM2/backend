package com.beautymeongdang.global.chatgpt.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatCompletionDto {

    private String model;

    private List<ChatRequestMsgDto> messages;

    @Builder
    public ChatCompletionDto(List<ChatRequestMsgDto> messages) {
        this.model = "gpt-3.5-turbo";
        this.messages = messages;
    }
}
