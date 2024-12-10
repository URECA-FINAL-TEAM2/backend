package com.beautymeongdang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerAddressResponseDto {
    private Long sidoId;
    private Long sigunguId;
    private String sidoName;
    private String sigunguName;
}
