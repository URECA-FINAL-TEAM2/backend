package com.beautymeongdang.domain.quote.repository;


import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.TotalQuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalQuoteRequestRepository extends JpaRepository<TotalQuoteRequest, Long> {

    // 고객(자신)이 보낸 견적 요청 상세 조회
    TotalQuoteRequest findByRequestId(QuoteRequest requestId);
}