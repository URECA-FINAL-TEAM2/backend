package com.beautymeongdang.domain.review.controller;

import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.service.ReviewService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReviews(@RequestPart CreateReviewRequestDto requestDto
            , @RequestPart(required = false) List<MultipartFile> images) {
        return ApiResponse.ok(200, reviewService.createReview(requestDto, images), "리뷰 작성 성공");
    }

    // 리뷰 수정
    @PutMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateReviews(
            @PathVariable(name = "reviewId") Long reviewId,
            @RequestPart UpdateReviewRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images) {
        return ApiResponse.ok(200, reviewService.updateReview(reviewId, requestDto, images), "리뷰 수정 성공");
    }

    // 리뷰 논리적 삭제
    @PutMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReviews(@PathVariable(name = "reviewId") Long reviewId) {
        return ApiResponse.ok(200, reviewService.deleteReview(reviewId), "리뷰 삭제 성공하였습니다.");
    }
    
    // 특정 고객 리뷰 리스트 조회
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<GetCustomerReviewListResponseDto>>> getCustomerReviews(@PathVariable(name = "customerId") Long customerId) {
        List<GetCustomerReviewListResponseDto> reviews = reviewService.getCustomerReviews(customerId);
        return ApiResponse.ok(200, reviews, "고객 리뷰 리스트 조회에 성공하였습니다.");
    }

}