package com.beautymeongdang.domain.chat.dto;

import com.beautymeongdang.domain.chat.entity.ChatMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatMessageResponseDto {
    private Long chatId;
    private Long senderId;
    private String senderNickname;
    private String senderProfileImage;
    private String content;
    private MessageType messageType;
    private Boolean customerYn;
    private Boolean isRead;
    private String imageUrl;
    private LocalDateTime createdAt;

}