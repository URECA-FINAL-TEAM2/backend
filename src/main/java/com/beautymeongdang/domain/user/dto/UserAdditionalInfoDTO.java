package com.beautymeongdang.domain.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAdditionalInfoDTO {
    private String roleCode;  // "010": 고객, "020": 미용사
    private String phone;
}
