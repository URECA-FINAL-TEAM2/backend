package com.beautymeongdang.domain.chat.pubsub;

import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;
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
            log.info("Redis 메시지 수신됨 - raw message: {}", message);

            // Redis에서 받은 메시지를 ChatMessage 객체로 변환
            CreateChatMessageResponseDto chatMessage = objectMapper.readValue(message, CreateChatMessageResponseDto.class);
            log.info("Redis 메시지 변환 완료 - chatId: {}, senderId: {}",
                    chatMessage.getChatId(), chatMessage.getSenderId());

            // 웹소켓을 통해 구독자들에게 메시지 전달
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatId(), chatMessage);

            log.info("Redis 메시지 WebSocket 전달 완료 - chatId: {}, messageType: {}",
                    chatMessage.getChatId(), chatMessage.getMessageType());
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패 - error: {}, raw message: {}",
                    e.getMessage(), message, e);
        }
    }
}