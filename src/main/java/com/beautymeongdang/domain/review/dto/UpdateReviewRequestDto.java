package com.beautymeongdang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewRequestDto {
    private BigDecimal starScore;
    private String content;
    private List<String> imageUrl;
}
