package com.beautymeongdang.domain.quote.repository;



import com.beautymeongdang.domain.quote.entity.TotalQuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalQuoteRequestRepository extends JpaRepository<TotalQuoteRequest, Long> {
    Optional<TotalQuoteRequest> findByRequestId_RequestId(Long requestId);}