package com.beautymeongdang.domain.review.service;


import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.entity.Reviews;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    // 리뷰 작성
    CreateUpdateReviewResponseDto createReview(CreateReviewRequestDto requestDto, List<MultipartFile> images);

    // 리뷰 수정
    CreateUpdateReviewResponseDto updateReview(Long reviewId, UpdateReviewRequestDto requestDto, List<MultipartFile> images);

    // 리뷰 논리적 삭제
    DeleteReviewResponseDto deleteReview(Long reviewId);

    // 특정 고객 리뷰 리스트 조회
        List<GetCustomerReviewListResponseDto> getCustomerReviews(Long customerId);

}