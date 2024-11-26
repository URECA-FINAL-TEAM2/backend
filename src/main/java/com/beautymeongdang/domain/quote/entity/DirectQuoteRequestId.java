package com.beautymeongdang.domain.quote.entity;

import com.beautymeongdang.domain.user.entity.Groomer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DirectQuoteRequestId {


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id",nullable = false)
    private QuoteRequest requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id",nullable = false)
    private Groomer groomerId;

}
