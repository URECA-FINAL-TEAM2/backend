package com.beautymeongdang.domain.review.service;


import com.beautymeongdang.domain.review.dto.CreateLikeRequestDto;
import com.beautymeongdang.domain.review.dto.CreateLikeResponseDto;
import com.beautymeongdang.domain.review.dto.DeleteLikeRequestDto;
import com.beautymeongdang.domain.review.dto.DeleteLikeResponseDto;

public interface ReviewService {

    // 좋아요 생성
    CreateLikeResponseDto createLike(Long customerId, CreateLikeRequestDto request);

    // 좋아요 취소
    DeleteLikeResponseDto deleteLike(Long customerId, DeleteLikeRequestDto request);
}