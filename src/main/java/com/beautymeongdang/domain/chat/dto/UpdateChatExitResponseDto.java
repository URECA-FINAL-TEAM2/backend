package com.beautymeongdang.domain.chat.dto;

import com.beautymeongdang.domain.chat.entity.Chat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateChatExitResponseDto {
    private Long chatId;
    private Long exitUserId;
    private String exitUserNickname;
    private Boolean customerYn;
    private String createdAt;

    public static UpdateChatExitResponseDto from(Chat chat, Long exitUserId, Boolean customerYn) {
        String nickname = customerYn ? 
            chat.getCustomerId().getUserId().getUserName() :
            chat.getGroomerId().getUserId().getNickname();
            
        return UpdateChatExitResponseDto.builder()
                .chatId(chat.getChatId())
                .exitUserId(exitUserId)
                .exitUserNickname(nickname)
                .customerYn(customerYn)
                .createdAt(chat.getCreatedAt().toString())
                .build();
    }
}