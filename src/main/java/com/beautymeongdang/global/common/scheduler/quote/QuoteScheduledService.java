package com.beautymeongdang.global.common.scheduler.quote;

import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteScheduledService {
    private final SelectedQuoteService selectedQuoteService;

    // 예약 상태 미용 완료 변경
    @Scheduled(cron = "0 0 * * * *") //매 시간마다 실행
    public void updateExpiredQuotesStatus() {
        selectedQuoteService.updateStatusToCompletedIfPastBeautyDate();
    }

}
