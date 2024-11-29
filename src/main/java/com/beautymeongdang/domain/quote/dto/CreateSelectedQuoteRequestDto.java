package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSelectedQuoteRequestDto {
    private Long quoteId;
    private Long customerId;
}
