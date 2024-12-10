package com.beautymeongdang.domain.chat.entity;

import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage extends DeletableBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id",nullable = false)
    private Chat chatId;
    private String content;
    private Boolean customerYn;

    public enum MessageType {
        ENTER,  // 채팅방 입장
        TALK,   // 일반 메시지
        QUIT    // 채팅방 나가기
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;
}