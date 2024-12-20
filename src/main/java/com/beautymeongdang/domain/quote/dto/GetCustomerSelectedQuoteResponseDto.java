package com.beautymeongdang.domain.quote.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter

public class GetCustomerSelectedQuoteResponseDto {
    private final Long selectedQuoteId;
    private final Long quoteId;
    private final Long groomerId;
    private final String profileImage;
    private final String shopName;
    private final String groomerName;
    private final LocalDateTime beautyDate;
    private final String dogName;
    private final String status;
    private final Boolean reviewCheck;

    @Builder
    public GetCustomerSelectedQuoteResponseDto(
            Long selectedQuoteId,
            Long quoteId,
            Long groomerId,
            String profileImage,
            String shopName,
            String groomerName,
            LocalDateTime beautyDate,
            String dogName,
            String status,
            Boolean reviewCheck) {
        this.selectedQuoteId = selectedQuoteId;
        this.quoteId = quoteId;
        this.groomerId = groomerId;
        this.profileImage = profileImage;
        this.shopName = shopName;
        this.groomerName = groomerName;
        this.beautyDate = beautyDate;
        this.dogName = dogName;
        this.status = status;
        this.reviewCheck = reviewCheck;
    }
}