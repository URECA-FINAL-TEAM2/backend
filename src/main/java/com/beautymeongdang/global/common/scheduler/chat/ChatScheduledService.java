package com.beautymeongdang.global.common.scheduler.chat;

import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatScheduledService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageImageRepository chatMessageImageRepository;

    // 채팅 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteChatMessages() {
        List<ChatMessage> chatMessages = chatMessageRepository.findDeletedMessagesBeforeDate(LocalDateTime.now().minusDays(30));

        chatMessages.forEach(chatMessage -> {
            List<ChatMessageImage> chatMessageImages = chatMessageImageRepository.findAllByMessageId(chatMessage);
            chatMessageImageRepository.deleteAll(chatMessageImages);

            chatMessageRepository.delete(chatMessage);
        });

    }

}