package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.TotalQuoteRequest;
import com.beautymeongdang.domain.quote.entity.TotalQuoteRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalQuoteRequestRepository extends JpaRepository<TotalQuoteRequest, TotalQuoteRequestId> {
}