package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetGroomerSelectedQuoteResponseDto {
    private final Long selectedQuoteId;
    private final Long quoteId;
    private final String profileImage;
    private final String customerName;
    private final String nickName;
    private final String phone;
    private final String dogName;
    private final LocalDateTime beautyDate;
    private final String status;
    private final String dogBreed;

    @Builder
    public GetGroomerSelectedQuoteResponseDto(
            Long selectedQuoteId,
            Long quoteId,
            String profileImage,
            String customerName,
            String nickName,
            String phone,
            String dogName,
            LocalDateTime beautyDate,
            String status,
            String dogBreed) {
        this.selectedQuoteId = selectedQuoteId;
        this.quoteId = quoteId;
        this.profileImage = profileImage;
        this.customerName = customerName;
        this.nickName = nickName;
        this.phone = phone;
        this.dogName = dogName;
        this.beautyDate = beautyDate;
        this.status = status;
        this.dogBreed = dogBreed;
    }
}