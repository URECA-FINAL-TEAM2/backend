package com.beautymeongdang.domain.quote.controller;

import com.beautymeongdang.domain.quote.dto.GetCustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GetSelectedQuoteDetailResponseDto;
import com.beautymeongdang.domain.quote.dto.GetGroomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/selected-quotes")
public class SelectedQuoteController {

    private final SelectedQuoteService selectedQuoteService;

    // 고객 예약 목록 조회
    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<GetCustomerSelectedQuoteResponseDto>>> getSelectedQuotesForCustomer(
            @RequestParam Long customerId) {
        List<GetCustomerSelectedQuoteResponseDto> reservations = selectedQuoteService.getSelectedQuotesForCustomer(customerId);
        return ApiResponse.ok(200, reservations, "고객 예약 목록 조회 성공");
    }

    // 미용사 예약 목록 조회
    @GetMapping("/groomer")
    public ResponseEntity<ApiResponse<List<GetGroomerSelectedQuoteResponseDto>>> getSelectedQuotesForGroomer(
            @RequestParam Long groomerId) {
        List<GetGroomerSelectedQuoteResponseDto> reservations = selectedQuoteService.getSelectedQuotesForGroomer(groomerId);
        return ApiResponse.ok(200, reservations, "미용사 예약 목록 조회 성공");
    }

    // 예약 상세 조회
    @GetMapping("/{selectedQuoteId}")
    public ResponseEntity<ApiResponse<GetSelectedQuoteDetailResponseDto>> getQuoteDetail(
            @PathVariable Long selectedQuoteId) {
        GetSelectedQuoteDetailResponseDto reservations = selectedQuoteService.getQuoteDetail(selectedQuoteId);
        return ApiResponse.ok(200, reservations, "예약 상세 조회 성공");
    }
}
