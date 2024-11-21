package com.beautymeongdang.domain.quote.entity;


import com.beautymeongdang.global.region.entity.Sigungu;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TotalQuoteRequest{

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private QuoteRequest requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Sigungu sigunguId;
}
