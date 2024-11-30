package com.beautymeongdang.domain.user.service.impl;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.user.dto.GetMainCustomerResponseDto.*;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.service.MainService;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainServiceImpl implements MainService {
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;


    @Override
    public MainResponse getMainPage(Long customerId) {
        // 베스트 리뷰 조회
        List<Reviews> bestReviews = reviewRepository.findTop2BestReviews();
        List<BestReviewDto> bestReviewDtos = bestReviews.stream()
                .map(review -> {
                    Shop shop = shopRepository.findByGroomerId(review.getGroomerId().getGroomerId())
                            .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

                    String reviewImage = reviewsImageRepository.findFirstImageUrlByReviewId(review.getReviewId())
                            .orElse("https://s3-beauty-meongdang.s3.ap-northeast-2.amazonaws.com/%EB%A6%AC%EB%B7%B0+%EC%9D%B4%EB%AF%B8%EC%A7%80/%EB%A9%94%EC%9D%B8%EB%A6%AC%EB%B7%B0%EB%85%B8%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg");

                    return BestReviewDto.builder()
                            .reviewId(review.getReviewId())
                            .shopName(shop.getShopName())
                            .reviewImage(reviewImage)
                            .content(review.getContent())
                            .starScore(review.getStarRating().doubleValue())
                            .recommendCount(reviewRepository.countRecommendsByReviewId(review.getReviewId()))
                            .build();
                })
                .collect(Collectors.toList());

        // 우리동네 미용사
        List<Shop> localGroomers = shopRepository.findShopsByCustomerSigunguOrderByReviewCountAndStarScore(customerId);
        List<LocalGroomerDto> localGroomerDtos = localGroomers.stream()
                .map(shop -> {
                    Long groomerId = shop.getGroomerId().getGroomerId();
                    return LocalGroomerDto.builder()
                            .groomerId(groomerId)
                            .shopId(shop.getShopId())
                            .shopLogo(shop.getImageUrl())
                            .shopName(shop.getShopName())
                            .starScoreAvg(shopRepository.getAverageStarRatingByGroomerId(groomerId))
                            .reviewCount(reviewRepository.countGroomerReviews(groomerId))
                            .address(shop.getAddress())
                            .businessTime(shop.getBusinessTime())
                            .skills(shop.getGroomerId().getSkill())
                            .build();
                })
                .collect(Collectors.toList());

        return MainResponse.builder()
                .bestReviews(bestReviewDtos)
                .localGroomers(localGroomerDtos)
                .build();
    }
}