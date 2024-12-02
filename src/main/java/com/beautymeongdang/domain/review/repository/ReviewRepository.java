package com.beautymeongdang.domain.review.repository;

import com.beautymeongdang.domain.review.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, Long> {

    //특정 미용사의 논리적 삭제되지 않은 모든 리뷰 조회
    @Query("SELECT r FROM Reviews r WHERE r.groomerId.groomerId = :groomerId AND r.isDeleted = false")
    List<Reviews> findGroomerReviews(@Param("groomerId") Long groomerId);


    //각 리뷰의 추천 수
    @Query("SELECT COUNT(r) FROM Recommend r WHERE r.recommendId.reviewId.reviewId = :reviewId AND r.isDeleted = false")
    Integer countRecommendsByReviewId(@Param("reviewId") Long reviewId);


    // Best 미용후기 2개
    @Query("""
            SELECT r FROM Reviews r
            JOIN FETCH r.groomerId g 
            WHERE r.isDeleted = false 
            ORDER BY 
            (SELECT COUNT(rec) FROM Recommend rec 
             WHERE rec.recommendId.reviewId = r 
             AND rec.isDeleted = false) DESC 
            LIMIT 2
            """)
    List<Reviews> findTop2BestReviews();


    //각 미용사의 리뷰 개수
    @Query("SELECT COUNT(r) FROM Reviews r WHERE r.groomerId.groomerId = :groomerId AND r.isDeleted = false")
    Integer countGroomerReviews(@Param("groomerId") Long groomerId);


}

