package com.beautymeongdang.domain.review.controller;

import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.service.LikeService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    /**
     * 좋아요 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateLikeResponseDto>> createLike(
            @RequestParam Long customerId,
            @RequestBody CreateLikeRequestDto request) {
        CreateLikeResponseDto response = likeService.createLike(customerId, request);
        return ApiResponse.ok(200, response, "리뷰 추천 성공하였습니다.");
    }

    /**
     * 좋아요 삭제
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteLikeResponseDto>> deleteLike(
            @RequestParam Long customerId,
            @RequestBody DeleteLikeRequestDto request) {
        DeleteLikeResponseDto response = likeService.deleteLike(customerId, request);
        return ApiResponse.ok(200, response, "리뷰 추천 취소 성공하였습니다.");
    }


}