package com.beautymeongdang.domain.chat.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessage extends BaseTimeEntity {

    private Long messageId;
    private Long chatId;
    private String content;
    private Boolean customerYn;

    @Builder
    public ChatMessage(Long messageId, Long chatId, String content, Boolean customerYn) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.content = content;
        this.customerYn = customerYn;
    }
}