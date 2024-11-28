package com.beautymeongdang.domain.review.repository;

import com.beautymeongdang.domain.review.entity.ReviewsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsImageRepository extends JpaRepository<ReviewsImage, Long> {

    //리뷰 이미지 찾기
    @Query("SELECT ri FROM ReviewsImage ri WHERE ri.reviewId.reviewId = :reviewId")
    List<ReviewsImage> findReviewImagesByReviewId(@Param("reviewId") Long reviewId);
}