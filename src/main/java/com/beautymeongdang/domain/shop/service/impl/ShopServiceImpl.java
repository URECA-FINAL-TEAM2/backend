package com.beautymeongdang.domain.shop.service.impl;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.RecommendRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.shop.dto.GetShopDetailResponseDto;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.shop.service.ShopService;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopServiceImpl implements ShopService {
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;
    private final GroomerRepository groomerRepository;
    private final GroomerPortfolioImageRepository groomerPortfolioImageRepository;
    private final ShopRepository shopRepository;
    private final RecommendRepository recommendRepository;


    /**
     * 매장 상세 조회
     */
    @Override
    public GetShopDetailResponseDto.ShopDetailResponseDto getShopDetail(Long groomerId, Long customerId) {
        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        List<String> portfolioImages = groomerPortfolioImageRepository.findImageUrlsByGroomerId(groomerId);

        List<Long> recommendedReviewIds = (customerId != null) ?
                recommendRepository.findReviewIdsByCustomerId(customerId) :
                Collections.emptyList();

        List<Reviews> reviews = reviewRepository.findGroomerReviews(groomerId);
        List<GetShopDetailResponseDto.ReviewDetailDto> reviewDtos = reviews.stream()
                .map(review -> {
                    Integer recommendCount = reviewRepository.countRecommendsByReviewId(review.getReviewId());
                    List<ReviewsImage> reviewImages = reviewsImageRepository.findReviewImagesByReviewId(review.getReviewId());
                    List<String> reviewImageUrls = reviewImages.stream()
                            .map(ReviewsImage::getImageUrl)
                            .collect(Collectors.toList());

                    return GetShopDetailResponseDto.ReviewDetailDto.builder()
                            .reviewId(review.getReviewId())
                            .customerNickname(review.getCustomerId().getUserId().getNickname())
                            .starScore(review.getStarRating().doubleValue())
                            .content(review.getContent())
                            .recommendCount(recommendCount)
                            .reviewsImage(reviewImageUrls)
                            .createdAt(review.getCreatedAt())
                            .recommended(recommendedReviewIds.contains(review.getReviewId()))
                            .build();
                })
                .collect(Collectors.toList());

        return GetShopDetailResponseDto.ShopDetailResponseDto.builder()
                .description(shop.getDescription())
                .shopImage(shop.getImageUrl())
                .groomerPortfolioImages(portfolioImages)
                .groomerUsername(groomer.getUserId().getNickname())
                .groomerProfileImage(groomer.getUserId().getProfileImage())
                .reviews(reviewDtos)
                .build();
    }
}