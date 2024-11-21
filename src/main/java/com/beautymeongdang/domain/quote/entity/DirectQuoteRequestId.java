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
    @JoinColumn(name = "request_id")
    private QuoteRequest request;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groomer_id")
    private Groomer groomer;

}
