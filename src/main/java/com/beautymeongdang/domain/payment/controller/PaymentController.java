package com.beautymeongdang.domain.payment.controller;

import com.beautymeongdang.domain.payment.dto.PaymentCancelRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentCancelResponseDto;
import com.beautymeongdang.domain.payment.dto.PaymentRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentResponseDto;
import com.beautymeongdang.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    // 결제 승인 요청
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDto> confirmPayment(@RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    // 결제 내역 상세 조회
    @GetMapping("/{paymentKey}")
    public ResponseEntity<PaymentResponseDto> getPaymentDetail(@PathVariable String paymentKey) {
        PaymentResponseDto paymentDetail = paymentService.getPaymentDetail(paymentKey);
        return ResponseEntity.ok(paymentDetail);
    }

    // 결제 취소
    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<PaymentCancelResponseDto> cancelPayment(
            @PathVariable String paymentKey,
            @RequestBody PaymentCancelRequestDto cancelDto) {
        cancelDto.setPaymentKey(paymentKey); // PathVariable에서 가져온 paymentKey 설정
        PaymentCancelResponseDto response = paymentService.cancelPayment(cancelDto);
        return ResponseEntity.ok(response);
    }
}
