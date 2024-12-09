package com.beautymeongdang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetCustomerChatListResponseDto {
    private Long roomId;
    private Long groomerId;
    private String groomerName;
    private String profileImage;
    private String shopName;
    private String ShopAddress;
    private String last_message;
    private LocalDateTime lastMessageTime;

    @Builder
    public GetCustomerChatListResponseDto(Long chatId, Long groomerId, String nickname,
                                          String profileImage, String shopName,
                                          String sidoName, String sigunguName,
                                          String lastMessageContent, LocalDateTime lastMessageCreatedAt) {
        this.roomId = chatId;
        this.groomerId = groomerId;
        this.groomerName = nickname;
        this.profileImage = profileImage;
        this.shopName = shopName;
        this.ShopAddress = sidoName + " " + sigunguName;
        this.last_message = lastMessageContent;
        this.lastMessageTime = lastMessageCreatedAt;
    }
}
