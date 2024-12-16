package com.beautymeongdang.domain.chat.dto;

import com.beautymeongdang.domain.chat.entity.ChatMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatMessageRequestDto {
    private Long chatId;
    private Long senderId;
    private String content;
    private MessageType messageType;
    private Boolean customerYn;
    private String base64Image;
}