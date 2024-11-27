package com.beautymeongdang.domain.quote.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSelectedQuoteResponseDto {
    private Long selectedQuoteId;
    private Long quoteId;
    private String profileImage;
    private String shopName;
    private String GroomerName;
    private String nickname;
    private LocalDateTime beautyDate;
    private String dogName;
    private String status;

    public CustomerSelectedQuoteResponseDto(Long selectedQuoteId, Long quoteId, String ProfileImage,
                                            String shopName, String nickname,
                                            LocalDateTime beautyDate, String dogName, String status) {
        this.selectedQuoteId = selectedQuoteId;
        this.quoteId = quoteId;
        this.profileImage = ProfileImage;
        this.shopName = shopName;
        this.nickname = nickname;
        this.beautyDate = beautyDate;
        this.dogName = dogName;
        this.status = status;
    }
}
