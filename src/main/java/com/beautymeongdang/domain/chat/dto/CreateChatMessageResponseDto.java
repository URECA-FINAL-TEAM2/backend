package com.beautymeongdang.domain.chat.dto;

import com.beautymeongdang.domain.chat.entity.Chat;
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
    private String imageUrl;
    private LocalDateTime createdAt;


    public static CreateChatResponseDto from(Chat chat) {
        return CreateChatResponseDto.builder()
                .chatId(chat.getChatId())
                .customerId(chat.getCustomerId().getCustomerId())
                .customerNickname(chat.getCustomerId().getUserId().getUserName()) //고객은 username
                .customerProfileImage(chat.getCustomerId().getUserId().getProfileImage())
                .groomerId(chat.getGroomerId().getGroomerId())
                .groomerNickname(chat.getGroomerId().getUserId().getNickname())
                .groomerProfileImage(chat.getGroomerId().getUserId().getProfileImage())
                .createdAt(chat.getCreatedAt().toString())
                .build();
    }

}