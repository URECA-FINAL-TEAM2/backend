package com.beautymeongdang.domain.review.entity;

import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewId;

    private String content;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal starRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id")
    private Groomer groomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_quote_id")
    private SelectedQuote selectedQuote;

}
