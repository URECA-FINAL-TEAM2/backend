package com.beautymeongdang.domain.shop.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteFavoriteResponseDto {
    private Long shopId;
}