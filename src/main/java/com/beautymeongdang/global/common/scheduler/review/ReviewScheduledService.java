package com.beautymeongdang.global.common.scheduler.review;

import com.beautymeongdang.domain.review.entity.Recommend;
import com.beautymeongdang.domain.review.entity.Reviews;
import com.beautymeongdang.domain.review.entity.ReviewsImage;
import com.beautymeongdang.domain.review.repository.RecommendRepository;
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
@Transactional
public class ReviewScheduledService {
    private final ReviewRepository reviewRepository;
    private final ReviewsImageRepository reviewsImageRepository;
    private final RecommendRepository recommendRepository;

    // 리뷰 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    public void deleteReview() {
        List<Reviews> reviews = reviewRepository.findAllByIsDeletedAndAndUpdatedAt(LocalDateTime.now().minusDays(30));

        reviews.forEach(review -> {
            List<ReviewsImage> reviewsImages = reviewsImageRepository.findAllByReviewId(review);
            reviewsImageRepository.deleteAll(reviewsImages);

            List<Recommend> recommends = recommendRepository.findAllByRecommendIdReviewId(review);
            recommendRepository.deleteAll(recommends);

            reviewRepository.delete(review);
        });

    }

}
