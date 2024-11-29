package com.beautymeongdang.domain.payment.service;

import com.beautymeongdang.domain.payment.dto.PaymentCancelRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentCancelResponseDto;
import com.beautymeongdang.domain.payment.dto.PaymentRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateSelectedQuoteRequestDto;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;

import java.util.List;

public interface PaymentService {

    Long createSelectedQuote(CreateSelectedQuoteRequestDto requestDto);
    PaymentResponseDto confirmPayment(PaymentRequestDto request); // 결제 승인 요청
    PaymentResponseDto getPaymentDetail(String paymentKey); // 결제 내역 상세 조회
    PaymentCancelResponseDto cancelPayment(PaymentCancelRequestDto request);// 결제 취소
}