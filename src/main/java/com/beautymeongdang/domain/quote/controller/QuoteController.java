package com.beautymeongdang.domain.quote.controller;


import com.beautymeongdang.domain.quote.dto.*;
import com.beautymeongdang.domain.quote.service.QuoteService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    /**
     * 고객이 보낸 1:1 견적 요청 조회
     */
    @GetMapping("requests/my/groomer")
    public ResponseEntity<ApiResponse<GetQuotesGroomerResponseDto>> getMyGroomerQuotes(
            @RequestParam Long customerId) {
        GetQuotesGroomerResponseDto responseDto = quoteService.getQuotesGroomer(customerId);
        return ApiResponse.ok(200, responseDto, "견적서(1:1) 요청 목록 조회 성공");
    }


    /**
     * 고객이 보낸 전체 견적 요청 조회
     */
    @GetMapping("requests/my/all")
    public ResponseEntity<ApiResponse<GetQuotesAllResponseDto>> getMyAllQuotes(
            @RequestParam Long customerId) {
        GetQuotesAllResponseDto responseDto = quoteService.getQuotesAll(customerId);
        return ApiResponse.ok(200, responseDto, "견적서(전체) 요청 목록 조회 성공");
    }


    /**
     * 고객이 받은 견적서 상세 조회 (요청+견적서)
     */
    @GetMapping("/detail/{quoteId}")
    public ResponseEntity<ApiResponse<GetQuoteDetailResponseDto>> getQuoteDetail(
            @PathVariable Long quoteId,
            @RequestParam Long customerId) {
        GetQuoteDetailResponseDto responseDto = quoteService.getQuoteDetail(quoteId,customerId);
        return ApiResponse.ok(200, responseDto, "견적서 상세 조회 성공");
    }

    // 미용사 견적서 작성
    @PostMapping("")
    public ResponseEntity<ApiResponse<CreateGroomerQuoteResponseDto>> createGroomerQuote(@RequestBody CreateGroomerQuoteRequestDto requestDto) {
        return ApiResponse.ok(200, quoteService.createGroomerQuote(requestDto), "Insert Quotes Success");
    }

    // 미용사가 보낸 견적서 상세 조회
    @GetMapping("/groomer/detail/{requestId}/{groomerId}")
    public ResponseEntity<ApiResponse<GetGroomerQuoteDetailResponseDto>> getGroomerQuoteDetail(@PathVariable Long requestId,
                                                                                               @PathVariable Long groomerId) {
        return ApiResponse.ok(200, quoteService.getGroomerQuoteDetail(requestId, groomerId), "견적서 상세 조회 성공");
    }

}
