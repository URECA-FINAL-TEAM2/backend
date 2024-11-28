package com.beautymeongdang.domain.quote.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter

public class GetCustomerSelectedQuoteResponseDto {
    private final Long selectedQuoteId;
    private final Long quoteId;
    private final String profileImage;
    private final String shopName;
    private final String groomerName;
    private final LocalDateTime beautyDate;
    private final String dogName;
    private final String status;

    @Builder
    public GetCustomerSelectedQuoteResponseDto(
            Long selectedQuoteId,
            Long quoteId,
            String profileImage,
            String shopName,
            String groomerName,
            LocalDateTime beautyDate,
            String dogName,
            String status) {
        this.selectedQuoteId = selectedQuoteId;
        this.quoteId = quoteId;
        this.profileImage = profileImage;
        this.shopName = shopName;
        this.groomerName = groomerName;
        this.beautyDate = beautyDate;
        this.dogName = dogName;
        this.status = status;
    }
}