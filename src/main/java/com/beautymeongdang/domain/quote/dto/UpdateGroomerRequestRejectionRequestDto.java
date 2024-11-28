package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroomerRequestRejectionRequestDto {
    private Long requestId;
    private Long groomerId;
    private String rejectionReason;
}
