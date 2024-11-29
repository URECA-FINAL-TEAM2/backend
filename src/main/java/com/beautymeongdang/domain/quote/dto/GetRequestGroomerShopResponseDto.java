package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetRequestGroomerShopResponseDto {
    private String shopImage;
    private String groomerName;
    private String shopName;
    private String address;
    private String phone;
}
