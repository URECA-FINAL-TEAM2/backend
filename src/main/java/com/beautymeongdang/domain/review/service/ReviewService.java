package com.beautymeongdang.domain.review.service;


import com.beautymeongdang.domain.review.dto.GetReviewResponseDto;


public interface ReviewService {
    //리뷰 조회
    GetReviewResponseDto.GroomerReviewResponseDto getGroomerReviews(Long groomerId);
}