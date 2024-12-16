package com.beautymeongdang.global.common.scheduler.quote;

import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.repository.QuoteRepository;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuoteScheduledService {
    private final SelectedQuoteService selectedQuoteService;
    private final QuoteRepository quoteRepository;

    // 예약 상태 미용 완료 변경
    @Scheduled(cron = "0 0 * * * *") //매 시간마다 실행
    public void updateExpiredQuotesStatus() {
        selectedQuoteService.updateStatusToCompletedIfPastBeautyDate();
    }

    // 견적서 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    public void deleteQuote() {
        List<Quote> quotes = quoteRepository.findAllByIsDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));
        quoteRepository.deleteAll(quotes);
    }

}
