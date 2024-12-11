package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetFavoriteShopListResponseDto {
    private Long groomerId;
    private Long shopId;
    private String shopLogo;
    private String shopName;
    private Double starScoreAvg;
    private Integer favoriteCount;
    private Integer reviewCount;
    private String address;
    private String businessTime;
    private String skill;
}
