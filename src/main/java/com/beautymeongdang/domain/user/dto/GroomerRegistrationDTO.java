package com.beautymeongdang.domain.user.dto;

import com.beautymeongdang.domain.shop.dto.ShopDTO;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GroomerRegistrationDTO {
    private String phone;
    // Groomer 정보
    private String skill;

    // Shop 정보
    private Long sigunguId;
    private String shopName;
    private String description;
    private String address;
    private String businessTime;
    private String imageUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
}