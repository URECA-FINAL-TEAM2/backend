package com.beautymeongdang.domain.quote.service;

import com.beautymeongdang.domain.quote.dto.CustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GroomerSelectedQuoteResponseDto;

import java.util.List;

public interface SelectedQuoteService {

    // 고객 예약 목록 조회
    List<CustomerSelectedQuoteResponseDto> getSelectedQuotesForCustomer(Long customerId);

    // 미용사 예약 목록 조회
    List<GroomerSelectedQuoteResponseDto> getSelectedQuotesForGroomer(Long groomerId);
}