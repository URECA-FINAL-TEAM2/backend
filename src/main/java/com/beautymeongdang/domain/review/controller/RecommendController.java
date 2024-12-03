package com.beautymeongdang.domain.review.controller;

import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.service.RecommendService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;

    /**
     * 좋아요 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateRecommendResponseDto>> createLike(
            @RequestParam Long customerId,
            @RequestBody CreateRecommendRequestDto request) {
        CreateRecommendResponseDto response = recommendService.createRecommend(customerId, request);
        return ApiResponse.ok(200, response, "리뷰 추천 성공하였습니다.");
    }

    /**
     * 좋아요 삭제
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteRecommendResponseDto>> deleteLike(
            @RequestParam Long customerId,
            @RequestBody DeleteRecommendRequestDto request) {
        DeleteRecommendResponseDto response = recommendService.deleteRecommend(customerId, request);
        return ApiResponse.ok(200, response, "리뷰 추천 취소 성공하였습니다.");
    }


}