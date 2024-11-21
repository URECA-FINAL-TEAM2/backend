package com.beautymeongdang.domain.quote.entity;

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

    @Column(nullable = false)
    private Long quoteId;

    @Column(nullable = false)
    private Long customerId;

    private String status;
}
