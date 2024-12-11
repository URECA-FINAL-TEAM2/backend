package com.beautymeongdang.domain.chat.dto;

import com.beautymeongdang.domain.chat.entity.ChatMessage.MessageType;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CreateChatMessageRequestDto {
    private Long chatId;
    private Long senderId;
    private String content;
    private MessageType messageType;
    private Boolean customerYn;
    private String base64Image;
}