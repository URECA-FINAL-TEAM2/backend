package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.global.common.entity.DeletableBaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Quote extends DeletableBaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private QuoteRequest quoteRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Dog dogId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer cost;

    private LocalDateTime beautyDate;

    private String status;

}