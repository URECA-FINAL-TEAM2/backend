package com.beautymeongdang.domain.chat.pubsub;

import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

// Redis로 메시지 발행
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(CreateChatMessageResponseDto message) {
        try {
            log.info("Redis 메시지 발행 시작 - chatId: {}, senderId: {}",
                    message.getChatId(), message.getSenderId());

            redisTemplate.convertAndSend(channelTopic.getTopic(), message);

            log.info("Redis 메시지 발행 완료 - chatId: {}, messageType: {}, content: {}",
                    message.getChatId(),
                    message.getMessageType(),
                    message.getContent());
        } catch (Exception e) {
            log.error("Redis 메시지 발행 실패 - chatId: {}, error: {}",
                    message.getChatId(), e.getMessage(), e);
        }
    }
}