package com.beautymeongdang.domain.payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    private String paymentKey;
    private String orderId;
    private Integer amount;
    private Long quoteId;
    private Long customerId;
}
