package com.beautymeongdang.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateReviewResponseDto {
    private Long reviewId;
    private Long groomerId;
    private Long customerId;
    private Long selectedQuoteId;
    private BigDecimal starScore;
    private String content;
    private List<String> reviewsImage;

    @Builder
    public CreateReviewResponseDto(Long reviewId, Long groomerId, Long customerId, Long selectedQuoteId,
                                   BigDecimal starScore, String content, List<String> reviewsImage) {
        this.reviewId = reviewId;
        this.groomerId = groomerId;
        this.customerId = customerId;
        this.selectedQuoteId = selectedQuoteId;
        this.starScore = starScore;
        this.content = content;
        this.reviewsImage = reviewsImage;
    }

}
