package com.beautymeongdang.domain.quote.service;

import com.beautymeongdang.domain.quote.dto.CreateInsertRequestAllResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestAllRequestDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestGroomerResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestGroomerRequestDto;
import org.springframework.web.multipart.MultipartFile;
import com.beautymeongdang.domain.quote.dto.GroomerDirectRequestListResponseDto;
import java.util.List;

public interface QuoteRequestService {

    // 전체 견적서 요청하기
    CreateInsertRequestAllResponseDto createInsertRequestAll(CreateInsertRequestAllRequestDto requestDto, List<MultipartFile> images);

    // 1:1 견적서 요청하기
    CreateInsertRequestGroomerResponseDto createInsertRequestGroomer(CreateInsertRequestGroomerRequestDto requestDto, List<MultipartFile> images);

    // 미용사가 받은 1:1 요청 조회
    List<GroomerDirectRequestListResponseDto> getGroomerDirectRequestList(Long groomerId);

}
