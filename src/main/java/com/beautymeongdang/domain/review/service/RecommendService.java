package com.beautymeongdang.domain.review.service;

import com.beautymeongdang.domain.review.dto.CreateRecommendRequestDto;
import com.beautymeongdang.domain.review.dto.CreateRecommendResponseDto;
import com.beautymeongdang.domain.review.dto.DeleteRecommendRequestDto;
import com.beautymeongdang.domain.review.dto.DeleteRecommendResponseDto;

public interface RecommendService {

    // 좋아요 생성
    CreateRecommendResponseDto createRecommend(Long customerId, CreateRecommendRequestDto request);

    // 좋아요 취소
    DeleteRecommendResponseDto deleteRecommend(Long customerId, DeleteRecommendRequestDto request);
}
