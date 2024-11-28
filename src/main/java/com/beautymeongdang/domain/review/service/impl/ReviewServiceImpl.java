package com.beautymeongdang.domain.review.service.impl;

import com.beautymeongdang.domain.review.dto.GetReviewResponseDto;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;


    /**
     * 리뷰 조회
     */
    @Override
    public GetReviewResponseDto.GroomerReviewResponseDto getGroomerReviews(Long groomerId) {
        List<Reviews> reviews = reviewRepository.findGroomerReviews(groomerId);

        List<GetReviewResponseDto.ReviewDetailDto> reviewDtos = reviews.stream()
                .map(review -> {
                    Integer recommendCount = reviewRepository.countRecommendsByReviewId(review.getReviewId());

                    List<ReviewsImage> reviewImages = reviewsImageRepository.findReviewImagesByReviewId(review.getReviewId());
                    List<String> reviewImageUrls = reviewImages.stream()
                            .map(ReviewsImage::getImageUrl)
                            .collect(Collectors.toList());

                    return GetReviewResponseDto.ReviewDetailDto.builder()
                            .reviewId(review.getReviewId())
                            .customerNickname(review.getCustomerId().getUserId().getNickname())
                            .starScore(review.getStarRating().doubleValue())
                            .content(review.getContent())
                            .recommendCount(recommendCount)
                            .reviewsImage(reviewImageUrls)
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return GetReviewResponseDto.GroomerReviewResponseDto.builder()
                .groomerId(groomerId)
                .reviews(reviewDtos)
                .build();
    }
}








