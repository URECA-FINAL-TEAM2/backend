package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GetShopResponseDto {
    private final Long shopId;
    private final String shopName;
    private final String description;
    private final String businessTime;
    private final String sidoName;
    private final String sigunguName;
    private final String address;
    private final String shopLogo;
    private final Integer favoriteCount;
    private final Integer reviewCount;
    private final List<String> groomerPortfolioImages;
}
