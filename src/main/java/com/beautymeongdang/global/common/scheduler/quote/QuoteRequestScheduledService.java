package com.beautymeongdang.global.common.scheduler.quote;

import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.QuoteRequestImage;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.*;
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
public class  QuoteRequestScheduledService {
    private final QuoteRequestRepository quoteRequestRepository;
    private final QuoteRequestImageRepository quoteRequestImageRepository;
    private final TotalQuoteRequestRepository totalQuoteRequestRepository;
    private final DirectQuoteRequestRepository directQuoteRequestRepository;
    private final FileStore fileStore;
    private final QuoteRepository quoteRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;


    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteQuoteRequest() {
        List<QuoteRequest> quoteRequests = quoteRequestRepository.findAllByIsDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));

        quoteRequests.forEach(quoteRequest -> {
            // 견적 요청 이미지 삭제
            List<QuoteRequestImage> images = quoteRequestImageRepository.findAllByRequestId(quoteRequest);
            images.forEach(image -> fileStore.deleteFile(image.getImageUrl()));
            quoteRequestImageRepository.deleteAll(images);

            // 전체 견적 요청 삭제
            totalQuoteRequestRepository.deleteByRequestId(quoteRequest);

            // 1:1 견적 요청 삭제
            directQuoteRequestRepository.deleteByDirectQuoteRequestIdRequestId(quoteRequest);

            // 견적 요청 삭제
            quoteRequestRepository.delete(quoteRequest);
        });
    }



    // 견적서 요청 했지만 3일동안 아무런 견적서 제안이 들어오지 않을 경우
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시에 실행
    @Transactional
    public void closeExpiredRequests() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        // 요청 상태이면서 4일이 지난 QuoteRequest 조회
        List<QuoteRequest> expiredRequests = quoteRequestRepository.findAllByStatusAndCreatedAtBefore("010", threeDaysAgo);

        // 상태를 마감으로 변경
        expiredRequests.forEach(request ->
                request.updateStatus("030") // 마감 상태 코드로 변경
        );

        quoteRequestRepository.saveAll(expiredRequests);
    }

    // 제안완료 상태에서 2일 동안 결제되지 않은 1:1 견적 요청을 마감으로 변경
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시에 실행
    public void closeUnpaidDirectRequests() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        // 1:1 요청("020")이면서 제안완료 상태("040")이고 2일이 지난 요청들 조회
        List<QuoteRequest> unpaidRequests = quoteRequestRepository.findAllByRequestTypeAndStatusAndUpdatedAtBefore("020", "040", twoDaysAgo);

        // 견적서가 있으면서 예약완료 상태가 아닌 요청들만 마감으로 변경
        unpaidRequests.stream()
                .filter(request -> {
                    Quote quote = quoteRepository.findByRequestId(request)
                            .orElse(null);
                    if (quote == null) return false;

                    SelectedQuote selectedQuote = selectedQuoteRepository.findByQuoteId(quote);

                    // 선택된 견적서가 없거나, 있더라도 예약완료(010) 상태가 아닌 경우
                    return selectedQuote == null || !selectedQuote.getStatus().equals("010");
                })
                .forEach(request ->
                        request.updateStatus("030") // 마감 상태 코드로 변경
                );

        quoteRequestRepository.saveAll(unpaidRequests);
    }



    // 전체요청에서 견적서가 선택되어 예약된 경우 해당 견적요청을 마감으로 변경
    @Scheduled(cron = "0 */10 * * * *")
    public void closeTotalRequestWhenQuoteSelected() {
        // 전체요청("010")이면서 요청상태("010")인 요청들 조회
        List<QuoteRequest> totalRequests = quoteRequestRepository.findAllByRequestTypeAndStatus(
                "010", "010");

        totalRequests.stream()
                .filter(request -> {
                    // 해당 견적요청에 대한 모든 견적서들 조회
                    List<Quote> quotes = quoteRepository.findAllByRequestId(request.getRequestId());

                    // 견적서들 중 하나라도 선택되어 예약완료 상태인지 확인
                    return quotes.stream().anyMatch(quote -> {
                        SelectedQuote selectedQuote = selectedQuoteRepository.findByQuoteId(quote);
                        return selectedQuote != null && selectedQuote.getStatus().equals("010"); // 예약완료
                    });
                })
                .forEach(request ->
                        request.updateStatus("030") // 마감 상태 코드로 변경
                );

        quoteRequestRepository.saveAll(totalRequests);
    }


}
