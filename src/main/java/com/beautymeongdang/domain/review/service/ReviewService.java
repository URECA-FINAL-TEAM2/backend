package com.beautymeongdang.domain.review.service;


import com.beautymeongdang.domain.review.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    // 좋아요 생성
    CreateLikeResponseDto createLike(Long customerId, CreateLikeRequestDto request);

    // 좋아요 취소
    DeleteLikeResponseDto deleteLike(Long customerId, DeleteLikeRequestDto request);

    // 리뷰 작성
    CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto, List<MultipartFile> images);

}