package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetQuoteDetailResponseDto {
    private GroomerInfo groomer;
    private QuoteRequestInfo quoteRequest;
    private QuoteInfo quote;
    
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroomerInfo {
        private String shopLogo;
        private String groomerName;
        private String shopName;
        private String address;
        private String phone;
    }
    
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuoteRequestInfo {
        private String dogName;
        private String dogImage;
        private String dogWeight;
        private String dogAge;
        private String dogGender;
        private String dogBreed;
        private Boolean neutering;
        private Boolean experience;
        private String significant;
        private String requestContent;
        private List<String> requestImage;
    }
    
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuoteInfo {
        private Long quoteId;
        private LocalDateTime beautyDate;
        private Integer cost;
        private String quoteContent;
    }
}