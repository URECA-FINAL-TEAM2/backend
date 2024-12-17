package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

public class GetGroomerShopListResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShopListResponse {
        private List<ShopDto> shopLists;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShopDto {
        private Long groomerId;
        private Long shopId;
        private String shopLogo;
        private String shopName;
        private Double starScoreAvg;
        private Integer reviewCount;
        private String address;
        private String businessTime;
        private String skills;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private Integer favoriteCount;
    }
}