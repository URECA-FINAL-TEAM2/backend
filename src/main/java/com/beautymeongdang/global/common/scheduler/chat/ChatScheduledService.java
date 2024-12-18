package com.beautymeongdang.global.common.scheduler.chat;

import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatScheduledService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageImageRepository chatMessageImageRepository;
    private final ChatRepository chatRepository;
    private final FileStore fileStore;


    // 채팅 물리적 삭제 스케줄러
    @Scheduled(cron = "0 10 2 * * *")
    @Transactional
    public void deleteDeletedChats() {
        List<Chat> deletedChats = chatRepository.findAllByDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));

        deletedChats.forEach(chat -> {
            List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatId(chat);
            chatMessages.forEach(chatMessage -> {
                List<ChatMessageImage> chatMessageImages = chatMessageImageRepository.findAllByMessageId(chatMessage);
                chatMessageImages.forEach(image -> fileStore.deleteFile(image.getImageUrl()));
                chatMessageImageRepository.deleteAll(chatMessageImages);

                chatMessageRepository.delete(chatMessage);
            });

            chatRepository.delete(chat);
        });
    }


}
