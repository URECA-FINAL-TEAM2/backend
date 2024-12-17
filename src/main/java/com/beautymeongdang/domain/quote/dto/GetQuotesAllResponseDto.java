package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 전체 견적 요청/견적서 조회
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetQuotesAllResponseDto {
    private List<QuoteRequestInfo> quoteRequests;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuoteRequestInfo {
        private Long quoteRequestId;
        private String requestStatus;
        private LocalDateTime beautyDate;
        private String dogName;
        private String dogImage;
        private String requestContent;
        private String region;
        private List<QuoteInfo> quotes;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuoteInfo {
        private Long quoteId;
        private Long shopId;
        private String shopName;
        private String shopLogo;
        private String groomerName;
        private String quoteStatus;
        private Integer cost;
        private String quoteContent;
        private LocalDateTime createdAt;
        private LocalDateTime expireDate;
    }
}