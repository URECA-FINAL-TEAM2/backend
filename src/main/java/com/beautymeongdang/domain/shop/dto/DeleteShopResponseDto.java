package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteShopResponseDto {
    private Long shopId;
    private String shopName;
}