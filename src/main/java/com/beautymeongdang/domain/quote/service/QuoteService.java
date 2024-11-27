package com.beautymeongdang.domain.quote.service;


import com.beautymeongdang.domain.quote.dto.*;

public interface QuoteService {

    GetQuotesGroomerResponseDto getQuotesGroomer(Long customerId);
    GetQuotesAllResponseDto getQuotesAll(Long customerId);
    GetQuoteDetailResponseDto getQuoteDetail(GetQuoteDetailRequestDto requestDto);
}
