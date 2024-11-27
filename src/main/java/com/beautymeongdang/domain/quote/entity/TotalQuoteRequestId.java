package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.global.region.entity.Sigungu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TotalQuoteRequestId {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private QuoteRequest requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sigungu_id", nullable = false)
    private Sigungu sigunguId;
}