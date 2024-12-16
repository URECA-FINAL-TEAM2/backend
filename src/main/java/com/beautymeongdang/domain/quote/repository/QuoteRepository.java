package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.user.entity.Groomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // 고객에게 들어온 견적서(전체) 목록을 조회
    // 요청 타입이 '010'(전체)인 견적만 조회하며, 생성일시 기준 내림차순으로 정렬
    @Query("""
        SELECT q FROM Quote q
        JOIN FETCH q.requestId qr
        JOIN FETCH q.groomerId g
        JOIN FETCH Shop s ON s.groomerId = g
        WHERE qr.requestId = :requestId
        AND qr.requestType = '010'
        AND q.isDeleted = false
        ORDER BY q.createdAt DESC
    """)
    List<Quote> findAllByRequestId(@Param("requestId") Long requestId);

    // 요청이 거절 상태("020")일 때는 rejectReason이 포함되고 quoteId는 null
    @Query("SELECT q FROM Quote q WHERE q.requestId = :requestId AND q.groomerId = :groomerId AND q.isDeleted = false")
    Quote findByRequestIdAndGroomerIdAndIsDeletedFalse(@Param("requestId") QuoteRequest requestId, @Param("groomerId") Groomer groomerId);


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

    @Query("SELECT q FROM Quote q WHERE q.dogId.customerId.customerId = :customerId AND q.isDeleted = false")
    List<Quote> findAllByCustomerDogs(@Param("customerId") Long customerId);

    // 미용사 프로필 논리적 삭제
    List<Quote> findAllByGroomerId(Groomer groomer);

    // 반려견 프로필 논리적 삭제
    List<Quote> findAllByDogId(Dog dog);

    // 견적서 물리적 삭제 스케줄러
    @Query("""
    SELECT q
    FROM Quote q
    WHERE q.isDeleted = true
      AND q.updatedAt < :deleteDay
    """)
    List<Quote> findAllByIsDeletedAndUpdatedAt(@Param("deleteDay") LocalDateTime deleteDay);

    // 견적서 진행상태 변경 스케줄러
    @Query("""
    SELECT q
    FROM Quote q
    WHERE q.isDeleted = false
      AND q.createdAt < :expiryDate
    """)
    List<Quote> findAllByIsDeletedAndCreated(@Param("expiryDate") LocalDateTime expiryDate);
}
