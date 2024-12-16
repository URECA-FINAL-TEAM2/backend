package com.beautymeongdang.domain.quote.repository;


import com.beautymeongdang.domain.quote.entity.QuoteRequest;

import com.beautymeongdang.domain.quote.entity.TotalQuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalQuoteRequestRepository extends JpaRepository<TotalQuoteRequest, Long> {
    Optional<TotalQuoteRequest> findByRequestId_RequestId(Long requestId);

    // 고객(자신)이 보낸 견적 요청 상세 조회
    TotalQuoteRequest findByRequestId(QuoteRequest requestId);


    // qoute Request 물리적 삭제 스케줄러
    void deleteByRequestId(QuoteRequest requestId);

}