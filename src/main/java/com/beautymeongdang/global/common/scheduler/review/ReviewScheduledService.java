package com.beautymeongdang.global.common.scheduler.review;

import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.ReviewRepository;
import com.beautymeongdang.domain.review.repository.ReviewsImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewScheduledService {
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;

    // 리뷰 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteReview() {
        List<Reviews> reviews = reviewRepository.findAllByIsDeletedAndAndUpdatedAt(LocalDateTime.now().minusDays(30));

        reviews.forEach(review -> {
            List<ReviewsImage> reviewsImages = reviewsImageRepository.findAllByReviewId(review);
            reviewsImageRepository.deleteAll(reviewsImages);

            reviewRepository.delete(review);
        });

    }

}
