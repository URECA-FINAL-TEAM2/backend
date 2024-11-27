package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelectedQuoteRepository extends JpaRepository<SelectedQuote, Long> {

    // 고객 ID로 예약 목록 조회
    @Query("SELECT sq FROM SelectedQuote sq " +
            "JOIN FETCH sq.quoteId q " +
            "JOIN FETCH q.groomerId g " +
            "JOIN FETCH Shop s ON s.groomerId = g " + // Shop과 Groomer 조인
            "WHERE sq.customerId.customerId = :customerId AND sq.isDeleted = false")
    List<SelectedQuote> findAllByCustomerId(@Param("customerId") Long customerId);


    // 미용사 ID로 예약 목록 조회
    @Query("SELECT sq FROM SelectedQuote sq " +
            "JOIN FETCH sq.quoteId q " +
            "JOIN FETCH sq.customerId c " +
            "WHERE q.groomerId.groomerId = :groomerId AND sq.isDeleted = false")
    List<SelectedQuote> findAllByGroomerId(@Param("groomerId") Long groomerId);
}
