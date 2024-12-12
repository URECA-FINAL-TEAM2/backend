package com.beautymeongdang.global.common.scheduler;

import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduledDataTransferService {

    private final SelectedQuoteService selectedQuoteService;
    private final StringRedisTemplate redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageImageRepository chatMessageImageRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void updateExpiredQuotesStatus() {
        selectedQuoteService.updateStatusToCompletedIfPastBeautyDate();
    }

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