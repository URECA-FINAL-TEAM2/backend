package com.beautymeongdang.domain.review.controller;

import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.service.ReviewService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // 리뷰 작성
    @PostMapping(value = "/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReviews(@RequestPart CreateReviewRequestDto requestDto
            , @RequestPart(required = false) List<MultipartFile> images) {
        return ApiResponse.ok(200, reviewService.createReview(requestDto, images), "리뷰 작성 성공");
    }

}