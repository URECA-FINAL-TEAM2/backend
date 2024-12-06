package com.beautymeongdang.domain.user.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CustomerRegisterRequestDTO {
    private String nickName;
    private String phone;
    private Long sidoId;
    private Long sigungoId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}