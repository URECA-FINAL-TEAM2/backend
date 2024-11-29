package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroomerQuoteRequestDto {
    private Long requestId;
    private Long groomerId;
    private Long dogId;
    private String quoteContent;
    private Integer quoteCost;
    private LocalDateTime beautyDate;
}
