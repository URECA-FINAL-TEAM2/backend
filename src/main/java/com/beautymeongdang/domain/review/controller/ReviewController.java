package com.beautymeongdang.domain.review.controller;


import com.beautymeongdang.domain.review.dto.GetReviewResponseDto;
import com.beautymeongdang.domain.review.service.ReviewService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 조회
     */
    @GetMapping("/{groomerId}")
    public ResponseEntity<ApiResponse<GetReviewResponseDto.GroomerReviewResponseDto>> getGroomerReviews(
            @PathVariable Long groomerId) {
        GetReviewResponseDto.GroomerReviewResponseDto response = reviewService.getGroomerReviews(groomerId);
        return ApiResponse.ok(200, response, "리뷰 목록 조회 성공");
    }


}
