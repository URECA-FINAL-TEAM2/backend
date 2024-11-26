package com.beautymeongdang.domain.payment.entity;

import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Payment extends DeletableBaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_quote_id",nullable = false)
    private SelectedQuote selectedQuoteId;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(columnDefinition = "TEXT")
    private String cancelReason;

    private Integer amount;

    private String method;

    private String status;

    private String paymentTitle;

    private LocalDateTime approvedAt;
}
