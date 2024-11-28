package com.beautymeongdang.domain.shop.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShopDTO {
    private String shopName;
    private String description;
    private Long sigunguId;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String businessTime;
    private String imageUrl;
}
