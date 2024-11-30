package com.beautymeongdang.domain.quote.controller;


import com.beautymeongdang.domain.quote.dto.*;
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
            @RequestParam Long customerId,
            @RequestPart CreateInsertRequestAllRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images) {
        CreateInsertRequestAllResponseDto responseDto = quoteRequestService.createInsertRequestAll(customerId, requestDto, images);
        return ApiResponse.ok(200, responseDto, "전체 견적서 요청 성공");
    }


    /**
     * 견적서(1:1) 요청하기
     */
    @PostMapping(value = "/groomer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateInsertRequestGroomerResponseDto>> createInsertRequestGroomer(
            @RequestParam Long customerId,
            @RequestPart CreateInsertRequestGroomerRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images) {
        CreateInsertRequestGroomerResponseDto responseDto = quoteRequestService.createInsertRequestGroomer(customerId, requestDto, images);
        return ApiResponse.ok(200, responseDto, "1:1 견적서 요청 성공");
    }


    /**
     * 선택한 반려견 정보 조회
     */
    @GetMapping("/dog/info")
    public ResponseEntity<ApiResponse<GetDogInfoResponseDto>> getDogInfo(
            @RequestParam Long customerId,
            @RequestBody GetDogInfoRequestDto requestDto) {
        GetDogInfoResponseDto responseDto = quoteRequestService.getDogInfo(requestDto.getDogId(), customerId);
        return ApiResponse.ok(200, responseDto, "선택한 반려견 조회 성공");
    }

    /**
     * 1:1 견적서 요청에서 미용사와 매장 정보 조회
     */
    @GetMapping("/groomer/{groomerId}/shop")
    public ResponseEntity<ApiResponse<GetRequestGroomerShopResponseDto>> getGroomerShopInfo(
            @PathVariable Long groomerId) {
        GetRequestGroomerShopResponseDto responseDto = quoteRequestService.getGroomerShopInfo(groomerId);
        return ApiResponse.ok(200, responseDto, "미용사 매장 정보 조회 성공");
    }


    // 미용사가 받은 1:1 요청 조회
    @GetMapping("/groomer/direct/{groomerId}")
    public ResponseEntity<?> getGroomerDirectRequestList(@PathVariable(name = "groomerId") Long groomerId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerDirectRequestList(groomerId), "Get DirectRequestGroomer Success");
    }

    // 미용사 매장 근처 견적서 요청 공고 조회
    @GetMapping("/groomer/total/{groomerId}")
    public ResponseEntity<?> getGroomerTotalRequestList(@PathVariable(name = "groomerId") Long groomerId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerTotalRequestList(groomerId), "Get TotalRequestGroomer Success");
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

    // 미용사 1:1 맞춤 견적 요청 거절
    @PutMapping("/groomer")
    public ResponseEntity<?> updateGroomerRequestRejection(@RequestBody UpdateGroomerRequestRejectionRequestDto dto) {
        return ApiResponse.ok(200, quoteRequestService.updateGroomerRequestRejection(dto), "Update RequestRejection Success");
    }

}
