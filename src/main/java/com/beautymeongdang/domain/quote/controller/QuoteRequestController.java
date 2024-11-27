package com.beautymeongdang.domain.quote.controller;


import com.beautymeongdang.domain.quote.dto.CreateInsertRequestAllResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestAllRequestDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestGroomerResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestGroomerRequestDto;
import com.beautymeongdang.domain.quote.service.QuoteRequestService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class QuoteRequestController {
    private final QuoteRequestService quoteRequestService;

    /**
     * 견적서(전체) 요청하기
     */
    @PostMapping(value = "/all", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateInsertRequestAllResponseDto>> createInsertRequestAll(
            @RequestPart CreateInsertRequestAllRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images) {
        CreateInsertRequestAllResponseDto responseDto = quoteRequestService.createInsertRequestAll(requestDto, images);
        return ApiResponse.ok(200, responseDto, "전체 견적서 요청 성공");
    }


    /**
     * 견적서(1:1) 요청하기
     */
    @PostMapping(value = "/groomer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateInsertRequestGroomerResponseDto>> createInsertRequestGroomer(
            @RequestPart CreateInsertRequestGroomerRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images) {
        CreateInsertRequestGroomerResponseDto responseDto = quoteRequestService.createInsertRequestGroomer(requestDto, images);
        return ApiResponse.ok(200, responseDto, "1:1 견적서 요청 성공");
    }




}
