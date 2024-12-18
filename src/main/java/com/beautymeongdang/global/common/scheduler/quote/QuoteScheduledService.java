package com.beautymeongdang.global.common.scheduler.quote;

import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.repository.QuoteRepository;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Scheduled(cron = "0 30 1 * * *")
    public void deleteQuote() {
        List<Quote> quotes = quoteRepository.findAllByIsDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));
        quoteRepository.deleteAll(quotes);
    }

    // 견적서 진행상태 변경 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    public void updateQuoteStatus() {
        List<Quote> quotes = quoteRepository.findAllByIsDeletedAndCreated(LocalDateTime.now().minusDays(2));
        List<Quote> updateQuotes = new ArrayList<>();
        quotes.forEach(quote -> {
            Quote updateQuote = Quote.builder()
                    .quoteId(quote.getQuoteId())
                    .requestId(quote.getRequestId())
                    .groomerId(quote.getGroomerId())
                    .dogId(quote.getDogId())
                    .content(quote.getContent())
                    .cost(quote.getCost())
                    .beautyDate(quote.getBeautyDate())
                    .status("030")
                    .build();
            updateQuotes.add(updateQuote);
        });
        quoteRepository.saveAll(updateQuotes);
    }

}
