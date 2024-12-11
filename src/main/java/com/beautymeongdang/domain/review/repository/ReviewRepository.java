package com.beautymeongdang.domain.review.repository;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.user.entity.Groomer;
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
    @Query("SELECT COUNT(r) FROM Recommend r WHERE r.recommendId.reviewId.reviewId = :reviewId")
    Integer countRecommendsByReviewId(@Param("reviewId") Long reviewId);

    // customer과 같은 시군구에 있고, 리뷰 추천 수가 많은 Best 미용후기 2개
    @Query("""
    SELECT DISTINCT r FROM Reviews r
    JOIN FETCH r.groomerId g
    JOIN Shop s ON s.groomerId = g
    JOIN Customer c ON c.customerId = :customerId
    WHERE s.sigunguId = c.sigunguId
    AND r.isDeleted = false
    AND s.isDeleted = false
    AND g.isDeleted = false
    ORDER BY (SELECT COUNT(rec) FROM Recommend rec WHERE rec.recommendId.reviewId = r)  DESC
    LIMIT 2
    """)
    List<Reviews> findTop2BestReviewsBySigungu(@Param("customerId") Long customerId);




    @Query("SELECT AVG(r.starRating) FROM Reviews r WHERE r.groomerId.groomerId = :groomerId AND r.isDeleted = false")
    Double getAverageStarRatingByGroomerId(@Param("groomerId") Long groomerId);


    // 각 미용사의 리뷰 개수
    @Query("SELECT COUNT(r) FROM Reviews r WHERE r.groomerId.groomerId = :groomerId AND r.isDeleted = false")
    Integer countGroomerReviews(@Param("groomerId") Long groomerId);

    // 특정 고객의 논리적 삭제되지 않은 모든 리뷰 조회
    @Query("""
    SELECT r FROM Reviews r
    JOIN FETCH r.groomerId g
    JOIN FETCH Shop s ON s.groomerId = g
    WHERE r.customerId.customerId = :customerId AND r.isDeleted = false
""")
    List<Reviews> findCustomerReviews(@Param("customerId") Long customerId);

    @Query("SELECT r FROM Reviews r WHERE r.customerId.customerId = :customerId AND r.isDeleted = false")
    List<Reviews> findAllByCustomerId(@Param("customerId") Long customerId);


    // 미용사 프로필 논리적 삭제
    List<Reviews> findAllByGroomerId(Groomer groomer);

    // 본인이 적은 리뷰 수
    @Query("SELECT COUNT(r) FROM Reviews r " +
            "WHERE r.customerId.customerId = :customerId " +
            "AND r.isDeleted = false")
    Integer countByCustomerId(@Param("customerId") Long customerId);
}

