package com.beautymeongdang.domain.user.service.impl;

import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.user.dto.GetMainCustomerResponseDto.*;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.dto.GetMainGroomerResponseDto;
import com.beautymeongdang.domain.user.dto.GetMainGroomerTotalRequestResponseDto;
import com.beautymeongdang.domain.user.service.MainService;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainServiceImpl implements MainService {
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final QuoteRequestRepository quoteRequestRepository;


    /**
     * 고객 메인 페이지 조회
     */
    @Override
    public MainResponse getMainPage(Long customerId) {
        // 베스트 미용후기 조회
        List<Reviews> bestReviews = reviewRepository.findTop2BestReviewsBySigungu(customerId);
        List<BestReviewDto> bestReviewDtos = bestReviews.stream()
                .map(review -> {
                    Shop shop = shopRepository.findByGroomerId(review.getGroomerId().getGroomerId())
                            .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

                    Long groomerId = shop.getGroomerId().getGroomerId();

                    String reviewImage = reviewsImageRepository.findFirstImageUrlByReviewId(review.getReviewId())
                            .orElse("https://s3-beauty-meongdang.s3.ap-northeast-2.amazonaws.com/%EB%A6%AC%EB%B7%B0+%EC%9D%B4%EB%AF%B8%EC%A7%80/%EB%A9%94%EC%9D%B8%EB%A6%AC%EB%B7%B0%EB%85%B8%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg");

                    return BestReviewDto.builder()
                            .groomerId(groomerId)
                            .shopId(shop.getShopId())
                            .reviewId(review.getReviewId())
                            .shopName(shop.getShopName())
                            .reviewImage(reviewImage)
                            .content(review.getContent())
                            .starScore(review.getStarRating().doubleValue())
                            .recommendCount(reviewRepository.countRecommendsByReviewId(review.getReviewId()))
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        // 우리동네 디자이너
        List<Shop> localGroomers = shopRepository.findShopsByCustomerSigunguOrderByReviewCountAndStarScore(customerId);
        List<LocalGroomerDto> localGroomerDtos = localGroomers.stream()
                .map(shop -> {
                    Long groomerId = shop.getGroomerId().getGroomerId();
                    Integer reviewCount = reviewRepository.countGroomerReviews(groomerId);
                    Double starScoreAvg = shopRepository.getAverageStarRatingByGroomerId(groomerId);
                    Integer favoriteCount = shopRepository.countFavoritesByShop(shop);

                    return LocalGroomerDto.builder()
                            .groomerId(groomerId)
                            .shopId(shop.getShopId())
                            .shopLogo(shop.getImageUrl())
                            .shopName(shop.getShopName())
                            .starScoreAvg(starScoreAvg != null ? starScoreAvg : 0.0)
                            .reviewCount(reviewCount)
                            .favoriteCount(favoriteCount)
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

    // 미용사 메인 페이지 조회
    @Override
    public GetMainGroomerResponseDto getMainGroomerPage(Long groomerId) {
        // 오늘의 예약 건수 조회
        Integer todayReservation = selectedQuoteRepository.countTodayReservations(groomerId, LocalDateTime.now());

        // 1:1 견적 요청 건수 조회
        Integer totalDirectRequest = quoteRequestRepository.countTotalDirectRequest(groomerId);

        // 오늘의 1:1 견적 요청 건수 조회
        Integer todayRequest = quoteRequestRepository.countTodayRequests(groomerId, LocalDateTime.now());

        // 견적서 미발송 건수 조회
        Integer unsentQuote = quoteRequestRepository.countUnsentQuote(groomerId);

        // 우리동네 견적 공고
        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용실"));

        List<GetMainGroomerTotalRequestResponseDto> totalRequest =
                quoteRequestRepository.findTop3LatestRequestsBySigunguId(shop.getSigunguId().getSigunguId());

        return GetMainGroomerResponseDto.builder()
                .todayReservation(todayReservation)
                .totalDirectRequest(totalDirectRequest)
                .todayRequest(todayRequest)
                .unsentQuote(unsentQuote)
                .totalRequest(totalRequest)
                .build();
    }
}