package com.beautymeongdang.domain.review.repository;

import com.beautymeongdang.domain.review.entity.Recommend;
import com.beautymeongdang.domain.review.entity.RecommendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Recommend, RecommendId> {

    //특정 고객(customerId)이 좋아요를 누른 목록을 조회
    @Query("SELECT r.recommendId.reviewId.reviewId FROM Recommend r " +
            "WHERE r.recommendId.customerId.customerId = :customerId")
    List<Long> findReviewIdsByCustomerId(@Param("customerId") Long customerId);


}
