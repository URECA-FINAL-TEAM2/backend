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

    @Builder
    public CreateChatCompletionDto(List<CreateChatRequestMsgDto> messages) {
        this.model = "gpt-3.5-turbo";
        this.messages = messages;
    }
}
