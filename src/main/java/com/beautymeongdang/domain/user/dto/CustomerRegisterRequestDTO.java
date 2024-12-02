package com.beautymeongdang.domain.user.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CustomerRegisterRequestDTO {
    private String nickName;
    private String phone;
    private Long sigunguId;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

}