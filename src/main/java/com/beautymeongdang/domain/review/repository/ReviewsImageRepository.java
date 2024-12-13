package com.beautymeongdang.domain.review.repository;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewsImageRepository extends JpaRepository<ReviewsImage, Long> {

    // 리뷰 이미지 찾기
    @Query("SELECT ri FROM ReviewsImage ri WHERE ri.reviewId.reviewId = :reviewId")
    List<ReviewsImage> findReviewImagesByReviewId(@Param("reviewId") Long reviewId);

    // 고객 메인 페이지 베스트리뷰에서 첫 번째 이미지 조회
    @Query("SELECT ri.imageUrl FROM ReviewsImage ri WHERE ri.reviewId.reviewId = :reviewId ORDER BY ri.reviewsImageId ASC LIMIT 1")
    Optional<String> findFirstImageUrlByReviewId(@Param("reviewId") Long reviewId);

    // 리뷰 수정
    void deleteAllByReviewId(Reviews reviewId);

    // 리뷰 물리적 삭제 스케줄러
    List<ReviewsImage> findAllByReviewId(Reviews review);
}