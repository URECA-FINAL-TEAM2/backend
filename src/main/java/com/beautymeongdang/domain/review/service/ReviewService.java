package com.beautymeongdang.domain.review.service;


import com.beautymeongdang.domain.review.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    // 리뷰 작성
    CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto, List<MultipartFile> images);

}