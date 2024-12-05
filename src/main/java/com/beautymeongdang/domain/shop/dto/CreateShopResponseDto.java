package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class CreateShopResponseDto {
    private final Long shopId;
    private final String shopName;
    private final String description;
    private final String businessTime;
    private final String sidoName;
    private final String sigunguName;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private String shopLogo;
}