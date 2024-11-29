package com.beautymeongdang.domain.review.controller;

import com.beautymeongdang.domain.review.dto.CreateLikeRequestDto;
import com.beautymeongdang.domain.review.dto.CreateLikeResponseDto;
import com.beautymeongdang.domain.review.dto.DeleteLikeRequestDto;
import com.beautymeongdang.domain.review.dto.DeleteLikeResponseDto;
import com.beautymeongdang.domain.review.service.ReviewService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    /**
     * 좋아요 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateLikeResponseDto>> createLike(
            @RequestParam Long customerId,
            @RequestBody CreateLikeRequestDto request) {
        CreateLikeResponseDto response = reviewService.createLike(customerId, request);
        return ApiResponse.ok(200, response, "리뷰 추천 성공하였습니다.");
    }

    /**
     * 좋아요 삭제
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteLikeResponseDto>> deleteLike(
            @RequestParam Long customerId,
            @RequestBody DeleteLikeRequestDto request) {
        DeleteLikeResponseDto response = reviewService.deleteLike(customerId, request);
        return ApiResponse.ok(200, response, "리뷰 추천 취소 성공하였습니다.");
    }
}