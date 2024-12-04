package com.beautymeongdang.domain.review.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateRecommendResponseDto {
    private Long reviewId;
}