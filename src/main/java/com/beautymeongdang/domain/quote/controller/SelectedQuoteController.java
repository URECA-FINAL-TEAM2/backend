package com.beautymeongdang.domain.quote.controller;

import com.beautymeongdang.domain.quote.dto.CustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GroomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/selected-quotes")
public class SelectedQuoteController {

    private final SelectedQuoteService selectedQuoteService;

    // 고객 예약 목록 조회
    @GetMapping("/customer")
    public ResponseEntity<List<CustomerSelectedQuoteResponseDto>> getSelectedQuotesForCustomer(@RequestParam Long customerId) {
        List<CustomerSelectedQuoteResponseDto> reservations = selectedQuoteService.getSelectedQuotesForCustomer(customerId);
        return ResponseEntity.ok(reservations);
    }

    // 미용사 예약 목록 조회
    @GetMapping("/groomer")
    public ResponseEntity<List<GroomerSelectedQuoteResponseDto>> getSelectedQuotesForGroomer(@RequestParam Long groomerId) {
        List<GroomerSelectedQuoteResponseDto> reservations = selectedQuoteService.getSelectedQuotesForGroomer(groomerId);
        return ResponseEntity.ok(reservations);
    }
}
