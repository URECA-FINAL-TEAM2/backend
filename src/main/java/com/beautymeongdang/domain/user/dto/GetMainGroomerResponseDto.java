package com.beautymeongdang.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMainGroomerResponseDto {
    private Integer todayReservation;
    private Integer totalDirectRequest;
    private Integer todayRequest;
    private Integer unsentQuote;
    private List<GetMainGroomerTotalRequestResponseDto> totalRequest;

}
