package com.beautymeongdang.domain.quote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuoteRequestImage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quoteRequestImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private QuoteRequest requestId;

    private String imageUrl;
}

