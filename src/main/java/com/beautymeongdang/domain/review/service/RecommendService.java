package com.beautymeongdang.domain.review.service;

import com.beautymeongdang.domain.review.dto.CreateRecommendResponseDto;
import com.beautymeongdang.domain.review.dto.DeleteRecommendResponseDto;

public interface RecommendService {

    // 리뷰 추천 생성
    CreateRecommendResponseDto createRecommend(Long customerId, Long reviewId);

    // 리뷰 추천 삭제
    DeleteRecommendResponseDto deleteRecommend(Long customerId, Long reviewId);
}
