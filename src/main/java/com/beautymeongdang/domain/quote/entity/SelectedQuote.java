package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SelectedQuote extends DeletableBaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectedQuoteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Quote quoteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customerId;

    private String status;
}
