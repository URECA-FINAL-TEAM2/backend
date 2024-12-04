package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetGroomerShopResponseDto {
    private final Long shopId;
    private final String shopName;
    private final String description;
    private final String businessTime;
    private final String sidoName;
    private final String sigunguName;
    private final String address;
    private final String shopLogo;
}
