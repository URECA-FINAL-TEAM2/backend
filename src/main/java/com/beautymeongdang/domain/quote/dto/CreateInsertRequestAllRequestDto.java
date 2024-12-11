package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 전체 미용사에게 견적 요청
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateInsertRequestAllRequestDto {
    private Long dogId;
    private String requestType;
    private String requestContent;
    private LocalDateTime beautyDate;
    private Long sigunguId;

}