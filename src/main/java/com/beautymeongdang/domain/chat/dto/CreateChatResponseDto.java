package com.beautymeongdang.domain.chat.dto;

import com.beautymeongdang.domain.chat.entity.Chat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatResponseDto {
    private Long chatId;
    private Long customerId;
    private String customerNickname;
    private String customerProfileImage;
    private Long groomerId;
    private String groomerNickname;
    private String groomerProfileImage;
    private String createdAt;

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