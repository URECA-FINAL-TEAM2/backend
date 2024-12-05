package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class UpdateShopResponseDto {
    private Long shopId;
    private String shopName;
    private String description;
    private String businessTime;
    private String sidoName;
    private String sigunguName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String shopLogo;
}