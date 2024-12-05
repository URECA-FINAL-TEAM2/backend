package com.beautymeongdang.domain.quote.service;


import com.beautymeongdang.domain.quote.dto.*;

public interface QuoteService {

    // 고객이 자기가 보낸 견적(1:1) 요청을 조회
    GetQuotesGroomerResponseDto getQuotesGroomer(Long customerId);

    // 고객이 자기가 보낸 견적(전체) 요청을 조회
    GetQuotesAllResponseDto getQuotesAll(Long customerId);

    // 고객이 받은 견적서 상세 조회 (요청+견적서)
    GetQuoteDetailResponseDto getQuoteDetail(Long quoteId, Long customerId);

    // 미용사 견적서 작성
    CreateGroomerQuoteResponseDto createGroomerQuote(CreateGroomerQuoteRequestDto requestDto);

    // 미용사가 보낸 견적서 상세 조회
    GetGroomerQuoteDetailResponseDto getGroomerQuoteDetail(GetGroomerQuoteDetailRequestDto requestDto);

}
