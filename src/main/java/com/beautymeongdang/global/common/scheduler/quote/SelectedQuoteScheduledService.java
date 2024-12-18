package com.beautymeongdang.global.common.scheduler.quote;

import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SelectedQuoteScheduledService {
    private final SelectedQuoteService selectedQuoteService;

    // 선택된 견적서 물리적 삭제
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteExpiredSelectedQuotes() {
        selectedQuoteService.deleteExpiredLogicalDeletedSelectedQuotes();
    }
}
