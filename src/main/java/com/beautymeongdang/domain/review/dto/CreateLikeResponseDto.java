package com.beautymeongdang.domain.review.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateLikeResponseDto {
    private Long reviewId;
}