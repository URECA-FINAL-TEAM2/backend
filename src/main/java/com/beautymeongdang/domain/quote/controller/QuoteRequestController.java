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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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


    // 미용사가 받은 1:1 요청 조회
    @GetMapping("/groomer/direct/{groomerId}")
    public ResponseEntity<?> getGroomerDirectRequestList(@PathVariable(name = "groomerId") Long groomerId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerDirectRequestList(groomerId), "Get DirectRequestGroomer Success");
    }

    // 미용사 매장 근처 견적서 요청 공고 조회
    @GetMapping("/groomer/total/{sigunguId}")
    public ResponseEntity<?> getGroomerTotalRequestList(@PathVariable(name = "sigunguId") Long sigunguId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerTotalRequestList(sigunguId), "Get TotalRequestGroomer Success");
    }

    // 미용사가 견적서 보낸 견적 요청 조회
    @GetMapping("/groomer/send/{groomerId}")
    public ResponseEntity<?> getGroomerSendQuoteRequest(@PathVariable(name = "groomerId") Long groomerId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerSendQuoteRequest(groomerId), "Get RequestGroomerSend Success");

    }

    // 미용사 견적서 요청 상세 조회
    @GetMapping("/groomer/detail/{requestId}")
    public ResponseEntity<?> getGroomerDetailQuoteRequest(@PathVariable(name = "requestId") Long requestId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerRequestDetail(requestId), "Get RequestDetail Success");
    }

}
