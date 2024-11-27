package com.beautymeongdang.domain.quote.service;

import com.beautymeongdang.domain.quote.dto.GroomerDirectRequestListResponseDto;

import java.util.List;

public interface QuoteRequestService {

    // 미용사가 받은 1:1 요청 조회
    List<GroomerDirectRequestListResponseDto> getGroomerDirectRequestList(Long groomerId);

}
