package com.beautymeongdang.global.common.scheduler.quote;

import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.QuoteRequestImage;
import com.beautymeongdang.domain.quote.repository.DirectQuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestImageRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.TotalQuoteRequestRepository;
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
            directQuoteRequestRepository.deleteByDirectQuoteRequestIdAndRequestId(quoteRequest);

            // 견적 요청 삭제
            quoteRequestRepository.delete(quoteRequest);
        });
    }
}
