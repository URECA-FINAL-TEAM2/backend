package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.domain.user.entity.Groomer;
import jakarta.persistence.*;
import lombok.*;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class DirectQuoteRequestId {


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id",nullable = false)
    private QuoteRequest requestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id",nullable = false)
    private Groomer groomerId;

}
