package com.beautymeongdang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

public class GetReviewResponseDto{

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GroomerReviewResponseDto {
        private Long groomerId;
        private List<ReviewDetailDto> reviews;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReviewDetailDto {
        private Long reviewId;
        private String customerNickname;
        private Double starScore;
        private String content;
        private Integer recommendCount;
        private List<String> reviewsImage;
        private LocalDateTime createdAt;
    }

}