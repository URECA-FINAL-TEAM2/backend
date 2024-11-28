package com.beautymeongdang.domain.payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelResponseDto {
    private String paymentKey;
    private String message;
    private String status;
    private String method;
    private String cancelReason;
    private Integer cancelAmount;
    private Long selectedQuoteId;
}
