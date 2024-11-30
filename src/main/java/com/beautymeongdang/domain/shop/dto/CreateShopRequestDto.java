package com.beautymeongdang.domain.shop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class CreateShopRequestDto {
    private final Long groomerId;
    private final String shopName;
    private final String description;
    private final String businessTime;
    private final String sidoName;
    private final String sigunguName;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String imageUrl;
}