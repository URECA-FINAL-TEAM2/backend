package com.beautymeongdang.domain.quote.service;

import com.beautymeongdang.domain.quote.dto.GetCustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GetSelectedQuoteDetailResponseDto;
import com.beautymeongdang.domain.quote.dto.GetGroomerSelectedQuoteResponseDto;

import java.util.List;

public interface SelectedQuoteService {

    // 고객 예약 목록 조회
    List<GetCustomerSelectedQuoteResponseDto> getSelectedQuotesForCustomer(Long customerId);

    // 미용사 예약 목록 조회
    List<GetGroomerSelectedQuoteResponseDto> getSelectedQuotesForGroomer(Long groomerId);

    // 예약 상세 조회
    GetSelectedQuoteDetailResponseDto getQuoteDetail(Long selectedQuoteId);

    // 미용 완료 변경
    void updateStatusToCompletedIfPastBeautyDate();
}