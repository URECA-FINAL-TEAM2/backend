package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.DirectQuoteRequest;
import com.beautymeongdang.domain.quote.entity.DirectQuoteRequestId;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectQuoteRequestRepository extends JpaRepository<DirectQuoteRequest, DirectQuoteRequestId> {

    // 특정 견적 요청서에 해당하는 DirectQuoteRequest 조회
    @Query("SELECT d FROM DirectQuoteRequest d WHERE d.directQuoteRequestId.requestId = :quoteRequest")
    Optional<DirectQuoteRequest> findByQuoteRequest(@Param("quoteRequest") QuoteRequest quoteRequest);
}
