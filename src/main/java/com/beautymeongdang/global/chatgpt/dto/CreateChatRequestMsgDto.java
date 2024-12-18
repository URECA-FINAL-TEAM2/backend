package com.beautymeongdang.global.chatgpt.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateChatRequestMsgDto {
    private String role;
    private String content;

    @Builder
    public CreateChatRequestMsgDto(String role, String content) {
        this.role = role;
        this.content = content;
    }
}