package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.QuoteRequestImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRequestImageRepository extends JpaRepository<QuoteRequestImage, Long> {

    // 특정 견적 요청 ID에 해당하는 모든 견적 요청 이미지를 조회.
    @Query("SELECT qri FROM QuoteRequestImage qri " +
           "WHERE qri.requestId.requestId = :requestId")
    List<QuoteRequestImage> findAllByRequestId(@Param("requestId") Long requestId);

    // qoute Request 물리적 삭제 스케줄러
    List<QuoteRequestImage> findAllByRequestId(QuoteRequest requestId);


}