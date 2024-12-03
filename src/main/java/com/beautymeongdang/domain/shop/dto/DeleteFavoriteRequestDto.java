package com.beautymeongdang.domain.shop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteFavoriteRequestDto {
    private Long customerId;
    private Long shopId;
}
