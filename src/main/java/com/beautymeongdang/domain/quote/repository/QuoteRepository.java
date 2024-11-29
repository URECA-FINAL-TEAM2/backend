package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.user.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // 고객에게 들어온 견적서(전체) 목록을 조회
    // 요청 타입이 '010'(전체)인 견적만 조회하며, 생성일시 기준 내림차순으로 정렬
    @Query("SELECT q FROM Quote q " +
            "JOIN FETCH q.requestId qr " +
            "WHERE qr.requestId = :requestId " +
            "AND qr.requestType = '010' " +
            "AND q.isDeleted = false " +
            "ORDER BY q.createdAt DESC")
    List<Quote> findAllByRequestId(@Param("requestId") Long requestId);


    // 견적서 상세 조회
    @Query("SELECT q FROM Quote q " +
            "JOIN FETCH q.requestId qr " +
            "JOIN FETCH q.groomerId c " +
            "JOIN FETCH qr.dogId d " +
            "WHERE q.quoteId = :quoteId " +
            "AND q.isDeleted = false")
    Optional<Quote> findQuoteDetailById(@Param("quoteId") Long quoteId);

    // 미용사가 보낸 견적서 상세 조회
    Quote findByRequestIdAndGroomerId(QuoteRequest requestId, Groomer groomerId);

}
