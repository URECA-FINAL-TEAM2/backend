package com.beautymeongdang.domain.review.service.impl;


import com.beautymeongdang.domain.review.dto.CreateRecommendRequestDto;
import com.beautymeongdang.domain.review.dto.CreateRecommendResponseDto;
import com.beautymeongdang.domain.review.dto.DeleteRecommendRequestDto;
import com.beautymeongdang.domain.review.dto.DeleteRecommendResponseDto;
import com.beautymeongdang.domain.review.entity.Recommend;
import com.beautymeongdang.domain.review.entity.RecommendId;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.repository.RecommendRepository;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.service.RecommendService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendServiceImpl implements RecommendService {

    private final ReviewRepository reviewRepository;
    private final RecommendRepository recommendRepository;
    private final CustomerRepository customerRepository;


    /**
     * 리뷰 추천 생성
     */
    @Override
    @Transactional
    public CreateRecommendResponseDto createRecommend(Long customerId, Long reviewId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("회원"));

        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> NotFoundException.entityNotFound("리뷰"));

        RecommendId recommendId = new RecommendId(customer, review);

        if (recommendRepository.existsById(recommendId)) {
            throw new BadRequestException("잘못된 요청입니다. 이미 추천한 리뷰입니다.");
        }

        if (review.getCustomerId().equals(customer)) {
            throw new BadRequestException("잘못된 요청입니다. 자신의 리뷰는 추천할 수 없습니다.");
        }

        Recommend recommend = Recommend.builder()
                .recommendId(recommendId)
                .build();

        recommendRepository.save(recommend);

        return CreateRecommendResponseDto.builder()
                .reviewId(reviewId)
                .build();
    }

    /**
     * 리뷰 추천 삭제
     */
    @Override
    @Transactional
    public DeleteRecommendResponseDto deleteRecommend(Long customerId, Long reviewId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("회원"));

        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> NotFoundException.entityNotFound("리뷰"));

        RecommendId recommendId = new RecommendId(customer, review);

        recommendRepository.deleteById(recommendId);

        return DeleteRecommendResponseDto.builder()
                .reviewId(reviewId)
                .build();
    }
}