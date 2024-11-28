package com.beautymeongdang.domain.quote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateGroomerRequestRejectionResponseDto {
    // quoteRequest
    private Long requestId;
    private Long dogId;
    private LocalDateTime beautyDate;
    private String requestContent;
    private String status;
    private String requestType;

    // directQuoteRequest
    private Long groomerId;
    private String rejectionReason;

    @Builder
    public UpdateGroomerRequestRejectionResponseDto(Long requestId, Long dogId, LocalDateTime beautyDate, String content,
                                                    String status, String requestType, Long groomerId, String reasonForRejection) {
        this.requestId = requestId;
        this.dogId = dogId;
        this.beautyDate = beautyDate;
        this.requestContent = content;
        this.status = status;
        this.requestType = requestType;
        this.groomerId = groomerId;
        this.rejectionReason = reasonForRejection;
    }

}
