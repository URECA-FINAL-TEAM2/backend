package com.beautymeongdang.domain.review.service.impl;

import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.review.dto.*;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import com.beautymeongdang.domain.review.service.ReviewService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final FileStore fileStore;
    private final ReviewsImageRepository reviewsImageRepository;


    // 리뷰 작성
    @Override
    @Transactional
    public CreateUpdateReviewResponseDto createReview(CreateReviewRequestDto requestDto, List<MultipartFile> images) {

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

        reviewsImageRepository.deleteAll(reviewsImageList);

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
}







