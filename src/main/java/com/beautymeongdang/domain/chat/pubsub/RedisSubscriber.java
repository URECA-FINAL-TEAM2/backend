package com.beautymeongdang.domain.chat.pubsub;

import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public void onMessage(String message) {
        try {
            log.info("Redis 메시지 수신: {}", message);
            CreateChatMessageResponseDto chatMessage = objectMapper.readValue(message, CreateChatMessageResponseDto.class);


            if ("MESSAGE_READ".equals(chatMessage.getContent())) {
                String readDestination = "/sub/chat/room/" + chatMessage.getChatId() + "/read";
                messagingTemplate.convertAndSend(readDestination, chatMessage);
                return;
            }

            if (ChatMessage.MessageType.ENTER.equals(chatMessage.getMessageType())) {
                return;
            }

            if (chatMessage.getMessageType() == ChatMessage.MessageType.TALK) {
                String destination = "/sub/chat/room/" + chatMessage.getChatId();
                messagingTemplate.convertAndSend(destination, chatMessage);
            }
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패", e);
        }
    }
}