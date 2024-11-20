package com.beautymeongdang.domain.payment.entity;

import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import com.beautymeongdang.global.common.entity.CommonCode;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends DeletableBaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Long selectedQuoteId;

    @Column(nullable = false)
    private Long paymentsKey;

    @Column(nullable = false)
    private Long orderId;
    private String amount;
    private String method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private CommonCode status;

    private String impUid;
    private LocalDateTime approvedAt;
}
