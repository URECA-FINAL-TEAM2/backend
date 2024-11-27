package com.beautymeongdang.domain.quote.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroomerSelectedQuoteResponseDto {
    private Long selectedQuoteId;
    private Long quoteId;
    private String profileImage;
    private String customerName;
    private String nickName;
    private String dogName;
    private LocalDateTime beautyDate;
    private String phone;
    private String status;
}
