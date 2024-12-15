package com.beautymeongdang.global.common.scheduler.chat;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
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
    private final ChatRepository chatRepository;

    // 채팅 메시지 물리적 삭제 스케줄러
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


    // 채팅 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteDeletedChats() {
        // 30일 전에 논리적으로 삭제된 채팅방 찾기
        List<Chat> deletedChats = chatRepository.findAllByDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));

        deletedChats.forEach(chat -> {
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatId(chat);
            chatMessages.forEach(chatMessage -> {
                // 각 채팅 메시지에 관련된 이미지 삭제
                List<ChatMessageImage> chatMessageImages = chatMessageImageRepository.findAllByMessageId(chatMessage);
                chatMessageImageRepository.deleteAll(chatMessageImages);

                // 채팅 메시지 삭제
                chatMessageRepository.delete(chatMessage);
            });

            // 채팅방 삭제
            chatRepository.delete(chat);
        });
    }


}
