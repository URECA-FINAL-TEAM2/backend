package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;


/**
 * 1:1 맞춤 견적 요청 조회
 */

@Getter
@Builder
@AllArgsConstructor
public class GetQuotesGroomerResponseDto {
    private final List<QuoteRequestInfo> quoteRequests;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class QuoteRequestInfo {
        private final Long quoteRequestId;
        private final String petName;
        private final String petImage;
        private final String status;
        private final String shopName;
        private final String groomerName;
        private final String beautyDate;
        private final String requestContent;
    }

}