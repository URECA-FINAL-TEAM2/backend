package com.beautymeongdang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateChatExitRequestDto {
    private Long chatId;
    private Long userId;
    private Boolean customerYn;
}