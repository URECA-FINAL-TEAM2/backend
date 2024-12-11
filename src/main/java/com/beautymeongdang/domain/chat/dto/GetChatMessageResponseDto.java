package com.beautymeongdang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GetChatMessageResponseDto {
    private Long messageId;
    private Boolean customerYn;
    private String messageContent;
    private String messageImage;
    private LocalDateTime messageTime;

    @Builder
    public GetChatMessageResponseDto(Long messageId, Boolean customerYn, String content,
                     LocalDateTime createdAt, String image_url) {
        this.messageId = messageId;
        this.customerYn = customerYn;
        this.messageContent = content;
        this.messageImage = image_url;
        this.messageTime = createdAt;
    }
}
