package com.beautymeongdang.domain.chat.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageMongo {

    @Id
    private String id;

    private Long messageId;
    private Long chatId;
    private Long senderId;
    private String content;
    private Boolean customerYn;
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private ChatMessage.MessageType messageType;

    private LocalDateTime createdAt;
}