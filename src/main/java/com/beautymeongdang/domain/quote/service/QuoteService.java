package com.beautymeongdang.domain.quote.service;


import com.beautymeongdang.domain.quote.dto.*;

public interface QuoteService {

    GetQuotesGroomerResponseDto getQuotesGroomer(Long customerId);
    GetQuotesAllResponseDto getQuotesAll(Long customerId);
    GetQuoteDetailResponseDto getQuoteDetail(GetQuoteDetailRequestDto requestDto);

    // 미용사 견적서 작성
    CreateGroomerQuoteResponseDto createGroomerQuote(CreateGroomerQuoteRequestDto requestDto);

    // 미용사가 보낸 견적서 상세 조회
    GetGroomerQuoteDetailResponseDto getGroomerQuoteDetail(GetGroomerQuoteDetailRequestDto requestDto);

}
