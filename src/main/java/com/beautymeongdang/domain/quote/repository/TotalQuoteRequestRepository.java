package com.beautymeongdang.domain.quote.repository;


import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.TotalQuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalQuoteRequestRepository extends JpaRepository<TotalQuoteRequest, Long> {

    // 미용사 프로필 물리적 삭제 스케줄러
    void deleteByRequestId(QuoteRequest quoteRequest);
}