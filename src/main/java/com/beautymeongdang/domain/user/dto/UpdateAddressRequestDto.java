package com.beautymeongdang.domain.user.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateAddressRequestDto {
    private String sidoName;
    private String sigunguName;
}
