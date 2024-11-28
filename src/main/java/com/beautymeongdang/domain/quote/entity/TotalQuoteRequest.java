package com.beautymeongdang.domain.quote.entity;


import com.beautymeongdang.global.region.entity.Sigungu;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TotalQuoteRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private QuoteRequest requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_id", nullable = false)
    private Sigungu sigunguId;
}