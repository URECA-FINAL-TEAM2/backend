package com.beautymeongdang.domain.review.service.impl;

import com.beautymeongdang.domain.notification.enums.NotificationType;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.review.service.ReviewService;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final GroomerRepository groomerRepository;
    private final ShopRepository shopRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final FileStore fileStore;
    private final ReviewsImageRepository reviewsImageRepository;
    private final NotificationService notificationService;


    // 리뷰 작성
    @Override
    @Transactional
    public CreateUpdateReviewResponseDto createReview(CreateReviewRequestDto requestDto, List<MultipartFile> images) {
        if (images!= null && images.size() > 3) {
            throw new BadRequestException("등록 가능한 리뷰 이미지 수를 초과하였습니다.");
        }

        Groomer groomer = groomerRepository.findById(requestDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Customer customer = customerRepository.findById(requestDto.getCustomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("회원"));

        SelectedQuote selectedQuote = selectedQuoteRepository.findById(requestDto.getSelectedQuoteId())
                .orElseThrow(() -> NotFoundException.entityNotFound("확정 견적서"));

        Reviews reviews = Reviews.builder()
                .groomerId(groomer)
                .customerId(customer)
                .selectedQuoteId(selectedQuote)
                .starRating(requestDto.getStarScore())
                .content(requestDto.getContent())
                .build();

        Reviews savedReview = reviewRepository.save(reviews);

        // 이미지 저장
        List<ReviewsImage> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.REVIEWS);

            List<ReviewsImage> reviewsImages = uploadedFiles.stream()
                    .map(uploadedFile -> ReviewsImage.builder()
                            .reviewId(savedReview)
                            .imageUrl(uploadedFile.getFileUrl())
                            .build())
                    .collect(Collectors.toList());

            savedImages = reviewsImageRepository.saveAll(reviewsImages);
        }

        // 알림 저장 로직 추가
        String notificationMessage = String.format(
                "매장에 리뷰가 작성되었습니다. 작성자: %s, 별점: %f, 리뷰 내용: %s",
                customer.getUserId().getUserName(),
                requestDto.getStarScore(),
                requestDto.getContent()
        );

        // 알림 저장
        notificationService.saveNotification(
                groomer.getUserId().getUserId(),
                "groomer",
                NotificationType.SHOP_REVIEW.getDescription(),
                notificationMessage
        );

        return CreateUpdateReviewResponseDto.builder()
                .reviewId(savedReview.getReviewId())
                .groomerId(savedReview.getGroomerId().getGroomerId())
                .customerId(savedReview.getCustomerId().getCustomerId())
                .selectedQuoteId(savedReview.getSelectedQuoteId().getSelectedQuoteId())
                .starScore(savedReview.getStarRating())
                .content(savedReview.getContent())
                .reviewsImage(savedImages.stream()
                        .map(ReviewsImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    // 리뷰 수정
    @Override
    @Transactional
    public CreateUpdateReviewResponseDto updateReview(Long reviewId, UpdateReviewRequestDto requestDto, List<MultipartFile> images) {
        if (images!= null && images.size() > 3) {
            throw new BadRequestException("등록 가능한 리뷰 이미지 수를 초과하였습니다.");
        }

        Reviews reviews = reviewRepository.findById(reviewId)
                .orElseThrow(() -> NotFoundException.entityNotFound("리뷰"));

        Reviews updateReview = Reviews.builder()
                .reviewId(reviewId)
                .content(requestDto.getContent())
                .starRating(requestDto.getStarScore())
                .groomerId(reviews.getGroomerId())
                .customerId(reviews.getCustomerId())
                .selectedQuoteId(reviews.getSelectedQuoteId())
                .build();

        Reviews savedReview = reviewRepository.save(updateReview);

        // 이미지 삭제 및 추가
        List<ReviewsImage> reviewsImageList = reviewsImageRepository.findReviewImagesByReviewId(reviewId);

        // 이미지 S3 삭제
        for (ReviewsImage reviewsImage : reviewsImageList) {
            fileStore.deleteFile(reviewsImage.getImageUrl());
        }

        // 이미지 DB 삭제
        reviewsImageRepository.deleteAllByReviewId(savedReview);

        List<ReviewsImage> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.REVIEWS);

            List<ReviewsImage> reviewsImages = uploadedFiles.stream()
                    .map(uploadedFile -> ReviewsImage.builder()
                            .reviewId(savedReview)
                            .imageUrl(uploadedFile.getFileUrl())
                            .build())
                    .collect(Collectors.toList());

            savedImages = reviewsImageRepository.saveAll(reviewsImages);
        }

        return CreateUpdateReviewResponseDto.builder()
                .reviewId(savedReview.getReviewId())
                .groomerId(savedReview.getGroomerId().getGroomerId())
                .customerId(savedReview.getCustomerId().getCustomerId())
                .selectedQuoteId(savedReview.getSelectedQuoteId().getSelectedQuoteId())
                .starScore(savedReview.getStarRating())
                .content(savedReview.getContent())
                .reviewsImage(savedImages.stream()
                        .map(ReviewsImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    // 리뷰 논리적 삭제
    @Override
    @Transactional
    public DeleteReviewResponseDto deleteReview(Long reviewId) {
        Reviews reviews = reviewRepository.findById(reviewId)
                .orElseThrow(() -> NotFoundException.entityNotFound("리뷰"));

        reviews.delete();

        return DeleteReviewResponseDto.builder()
                .reviewId(reviews.getReviewId())
                .build();
    }

    // 특정 고객 리뷰 리스트 조회
    @Override
    public List<GetCustomerReviewListResponseDto> getCustomerReviews(Long customerId) {
        List<Reviews> reviews = reviewRepository.findCustomerReviews(customerId);

        return reviews.stream()
                .map(review -> {
                    Groomer groomer = review.getGroomerId();
                    Shop shop = shopRepository.findByGroomerId(groomer.getGroomerId())
                            .orElseThrow(() -> new NotFoundException("매장을 찾을 수 없습니다: " + groomer.getGroomerId()));
                    Double averageStarRating = shopRepository.getAverageStarRatingByGroomerId(groomer.getGroomerId());

                    List<String> reviewImages = reviewsImageRepository.findReviewImagesByReviewId(review.getReviewId())
                            .stream()
                            .map(ReviewsImage::getImageUrl)
                            .collect(Collectors.toList());

                    return GetCustomerReviewListResponseDto.builder()
                            .reviewId(review.getReviewId())
                            .content(review.getContent())
                            .shopName(shop.getShopName())
                            .groomerName(groomer.getUserId().getNickname())
                            .reviewCount(reviewRepository.countGroomerReviews(groomer.getGroomerId()))
                            .starRating(BigDecimal.valueOf(averageStarRating))
                            .reviewDate(review.getCreatedAt().toLocalDate())
                            .groomerId(groomer.getGroomerId())
                            .customerId(review.getCustomerId().getCustomerId())
                            .reviewImages(reviewImages)
                            .build();
                })
                .collect(Collectors.toList());
    }
}







