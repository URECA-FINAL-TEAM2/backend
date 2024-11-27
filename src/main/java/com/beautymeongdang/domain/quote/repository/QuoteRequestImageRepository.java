package com.beautymeongdang.domain.quote.repository;

import com.beautymeongdang.domain.quote.entity.QuoteRequestImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRequestImageRepository extends JpaRepository<QuoteRequestImage, Long> {
    @Query("SELECT qri FROM QuoteRequestImage qri " +
           "WHERE qri.requestId.requestId = :requestId")
    List<QuoteRequestImage> findAllByRequestId(@Param("requestId") Long requestId);
}