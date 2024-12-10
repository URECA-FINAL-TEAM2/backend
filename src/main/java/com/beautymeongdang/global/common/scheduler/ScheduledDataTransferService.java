package com.beautymeongdang.global.common.scheduler;

import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduledDataTransferService {

    private final SelectedQuoteService selectedQuoteService;
    private final StringRedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void updateExpiredQuotesStatus() {
        selectedQuoteService.updateStatusToCompletedIfPastBeautyDate();
    }

}