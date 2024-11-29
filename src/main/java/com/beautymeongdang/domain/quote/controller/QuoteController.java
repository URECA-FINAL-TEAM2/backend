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
            @RequestParam Long customerId) {  // 나중에 이 부분을 로그인 유저 정보로 변경
        GetQuotesGroomerResponseDto responseDto = quoteService.getQuotesGroomer(customerId);
        return ApiResponse.ok(200, responseDto, "견적서(1:1) 요청 목록 조회 성공");
    }


    /**
     * 고객이 보낸 전체 견적 요청 조회
     */
    @GetMapping("requests/my/all")
    public ResponseEntity<ApiResponse<GetQuotesAllResponseDto>> getMyAllQuotes(
            @RequestParam Long customerId) {  // 나중에 이 부분을 로그인 유저 정보로 변경
        GetQuotesAllResponseDto responseDto = quoteService.getQuotesAll(customerId);
        return ApiResponse.ok(200, responseDto, "견적서(전체) 요청 목록 조회 성공");
    }


    /**
     * 미용사가 보낸 견적서 상세 조회
     */
    @GetMapping("/detail/{quoteId}")
    public ResponseEntity<ApiResponse<GetQuoteDetailResponseDto>> getQuoteDetail(
            @PathVariable Long quoteId) {
        GetQuoteDetailRequestDto requestDto = new GetQuoteDetailRequestDto(quoteId);
        GetQuoteDetailResponseDto responseDto = quoteService.getQuoteDetail(requestDto);
        return ApiResponse.ok(200, responseDto, "견적서 상세 조회 성공");
    }

    // 미용사 견적서 작성
    @PostMapping("")
    public ResponseEntity<?> createGroomerQuote(@RequestBody CreateGroomerQuoteRequestDto requestDto) {
        return ApiResponse.ok(200, quoteService.createGroomerQuote(requestDto), "Insert Quotes Success");
    }

}
