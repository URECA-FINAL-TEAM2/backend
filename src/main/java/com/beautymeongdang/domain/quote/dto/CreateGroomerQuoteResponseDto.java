package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateGroomerQuoteResponseDto {
    private Long quoteId;
    private Long requestId;
    private Long groomerId;
    private Long dogId;
    private String quoteContent;
    private Integer quoteCost;
    private LocalDateTime beautyDate;
    private String quoteStatus;

    @Builder
    public CreateGroomerQuoteResponseDto(Long quoteId, Long requestId, Long groomerId, Long dogId, String quoteContent,
                                         Integer quoteCost, LocalDateTime beautyDate, String quoteStatus) {
        this.quoteId = quoteId;
        this.requestId = requestId;
        this.groomerId = groomerId;
        this.dogId = dogId;
        this.quoteContent = quoteContent;
        this.quoteCost = quoteCost;
        this.beautyDate = beautyDate;
        this.quoteStatus = quoteStatus;
    }
}
