package com.beautymeongdang.domain.payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelRequestDto {
    private String paymentKey;
    private String cancelReason;
}
