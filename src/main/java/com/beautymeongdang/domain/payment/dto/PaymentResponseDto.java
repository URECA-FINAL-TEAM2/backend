package com.beautymeongdang.domain.payment.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private String paymentKey;
    private String message;
    private String status;
    private String method;
    private OffsetDateTime approvedAt;
    private Integer amount;
    private Long selectedQuoteId;
}
