package com.beautymeongdang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetGroomerChatListResponseDto {
    private Long roomId;
    private Long customerId;
    private String customerName;
    private String profileImage;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    @Builder
    public GetGroomerChatListResponseDto(Long chatId, Long customerId, String userName, String profileImage,
                                         String content, LocalDateTime createdAt) {
        this.roomId = chatId;
        this.customerId = customerId;
        this.customerName = userName;
        this.profileImage = profileImage;
        this.lastMessage = content;
        this.lastMessageTime = createdAt;
    }
}
