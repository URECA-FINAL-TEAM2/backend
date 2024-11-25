package com.beautymeongdang.domain.shop.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShopDTO {
    private String shopName;        // 이름
    private String description;     // 매장 설명
    private Long sigunguId;         // 시군구 id 
    private String address;         // 주소명
    private BigDecimal latitude;    // 위도 
    private BigDecimal longitude;   // 경도
    private String businessTime;    // 운영 시간
    private String imageUrl;        // 매장 로고 사진
}
