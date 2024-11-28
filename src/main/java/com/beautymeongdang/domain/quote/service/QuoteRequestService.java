package com.beautymeongdang.domain.quote.service;

import com.beautymeongdang.domain.quote.dto.*;
import org.springframework.web.multipart.MultipartFile;
import com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto;

import java.util.List;

public interface QuoteRequestService {

    // 전체 견적서 요청하기
    CreateInsertRequestAllResponseDto createInsertRequestAll(CreateInsertRequestAllRequestDto requestDto, List<MultipartFile> images);

    // 1:1 견적서 요청하기
    CreateInsertRequestGroomerResponseDto createInsertRequestGroomer(CreateInsertRequestGroomerRequestDto requestDto, List<MultipartFile> images);

    // 미용사가 받은 1:1 요청 조회
    List<GetGroomerQuoteRequestResponseDto> getGroomerDirectRequestList(Long groomerId);

    // 미용사 매장 근처 견적서 요청 공고 조회
    List<GetGroomerQuoteRequestResponseDto> getGroomerTotalRequestList(Long sigunguId);

    // 미용사가 견적서 보낸 견적 요청 조회
    List<GetGroomerSendQuoteRequestResponseDto> getGroomerSendQuoteRequest(Long groomerId);

}
