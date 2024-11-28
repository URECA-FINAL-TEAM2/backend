package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
        private String status;
        private String beautyDate;
        private String dogName;
        private String image;
        private String dogWeight;
        private String dogBreed;
        private String dogAge;
        private String requestContent;
        private List<QuoteInfo> quotes;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuoteInfo {
        private Long quoteId;
        private String shopName;
        private String groomerName;
        private String status;
        private Integer cost;
        private String quoteContent;
        private String createAt;
    }
}