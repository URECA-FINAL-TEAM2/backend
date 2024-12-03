package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFavoriteRequestDto {
    private Long customerId;
    private Long shopId;
}
