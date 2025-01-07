package com.beautymeongdang.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUnreadMessageCountResponseDto {
    private Long chatId;
    private Long unreadCount;
}