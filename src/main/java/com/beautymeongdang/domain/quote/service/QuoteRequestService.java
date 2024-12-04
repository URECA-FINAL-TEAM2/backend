package com.beautymeongdang.domain.quote.service;

import com.beautymeongdang.domain.quote.dto.*;
import org.springframework.web.multipart.MultipartFile;
import com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto;

import java.util.List;

public interface QuoteRequestService {

    // 전체 견적서 요청하기
    CreateInsertRequestAllResponseDto createInsertRequestAll(Long customerId, CreateInsertRequestAllRequestDto requestDto, List<MultipartFile> images);

    // 1:1 견적서 요청하기
    CreateInsertRequestGroomerResponseDto createInsertRequestGroomer(Long customerId, CreateInsertRequestGroomerRequestDto requestDto, List<MultipartFile> images);

    // 고객의 반려견 리스트 조회
    List<GetDogListResponseDto> getDogList(Long customerId);

    // 고객이 선택한 반려견 정보 조회
    GetDogInfoResponseDto getDogInfo(Long dogId, Long customerId);

    //  1:1 견적서 요청에서 미용사와 매장 정보 조회
    GetRequestGroomerShopResponseDto getGroomerShopInfo(Long groomerId);

    // 미용사가 받은 1:1 요청 조회
    List<GetGroomerQuoteRequestResponseDto> getGroomerDirectRequestList(Long groomerId);

    // 미용사 매장 근처 견적서 요청 공고 조회
    List<GetGroomerQuoteRequestResponseDto> getGroomerTotalRequestList(Long groomerId);

    // 미용사가 견적서 보낸 견적 요청 조회
    List<GetGroomerSendQuoteRequestResponseDto> getGroomerSendQuoteRequest(Long groomerId);

    // 미용사 견적서 요청 상세 조회
    GetGroomerRequestDetailResponseDto getGroomerRequestDetail(Long requestId);

    // 미용사 1:1 맞춤 견적 요청 거절
    UpdateGroomerRequestRejectionResponseDto updateGroomerRequestRejection(UpdateGroomerRequestRejectionRequestDto requestDto);

}
