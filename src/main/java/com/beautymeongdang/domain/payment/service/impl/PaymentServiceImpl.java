package com.beautymeongdang.domain.payment.service.impl;

import com.beautymeongdang.domain.payment.dto.PaymentCancelRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentCancelResponseDto;
import com.beautymeongdang.domain.payment.dto.PaymentRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentResponseDto;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.payment.repository.PaymentRepository;
import com.beautymeongdang.domain.payment.service.PaymentService;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${toss.payments.secret.key}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ShopRepository shopRepository;
    private final GroomerRepository groomerRepository;

    private static final String TOSS_PAYMENTS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    @Override
    public PaymentResponseDto confirmPayment(PaymentRequestDto request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        Map<String, Object> body = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(TOSS_PAYMENTS_CONFIRM_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                OffsetDateTime approvedAtOffset = OffsetDateTime.parse(responseBody.get("approvedAt").toString());
                LocalDateTime approvedAt = approvedAtOffset.toLocalDateTime();

                String method = responseBody.get("method").toString();

                // 선택된 견적서 조회
                SelectedQuote selectedQuote = selectedQuoteRepository.findById(request.getSelectedQuoteId())
                        .orElseThrow(() -> new IllegalArgumentException("선택된 견적서를 찾을 수 없습니다."));

                Long groomerId = selectedQuote.getQuoteId().getGroomerId().getGroomerId(); // GroomerId 조회

                String shopName = shopRepository.findByGroomerId(groomerId)
                        .orElseThrow(() -> new IllegalArgumentException("샵 정보를 찾을 수 없습니다."))
                        .getShopName();

                Payment payment = Payment.builder()
                        .paymentKey(request.getPaymentKey())
                        .orderId(request.getOrderId())
                        .amount(request.getAmount())
                        .method(method)
                        .status("결제 완료")
                        .approvedAt(approvedAt)
                        .selectedQuoteId(selectedQuote)
                        .paymentTitle(shopName)
                        .build();
                paymentRepository.save(payment);

                return PaymentResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .orderId(request.getOrderId())
                        .status("결제 완료")
                        .method(method)
                        .approvedAt(approvedAtOffset)
                        .amount(request.getAmount())
                        .selectedQuoteId(request.getSelectedQuoteId())
                        .message("결제 승인 성공")
                        .paymentTitle(shopName)
                        .build();
            } else {
                return PaymentResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("FAILED")
                        .message("결제 승인 실패: " + response.getStatusCode())
                        .build();
            }
        } catch (Exception e) {
            return PaymentResponseDto.builder()
                    .paymentKey(request.getPaymentKey())
                    .status("ERROR")
                    .message("결제 승인 중 오류 발생: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentCancelResponseDto cancelPayment(PaymentCancelRequestDto request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, ""); // 토스 API 인증

        Map<String, Object> body = Map.of(
                "cancelReason", request.getCancelReason()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            String url = "https://api.tosspayments.com/v1/payments/" + request.getPaymentKey() + "/cancel";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
                        .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

                payment = payment.toBuilder()
                        .status("결제 취소")
                        .cancelReason(request.getCancelReason())
                        .build();

                paymentRepository.save(payment);

                return PaymentCancelResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("결제 취소")
                        .method(payment.getMethod())
                        .cancelReason(request.getCancelReason())
                        .selectedQuoteId(payment.getSelectedQuoteId().getSelectedQuoteId()) // 필드 이름 유지
                        .message("결제 취소 성공")
                        .build();
            } else {
                return PaymentCancelResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("FAILED")
                        .message("결제 취소 실패: " + response.getStatusCode())
                        .build();
            }
        } catch (Exception e) {
            return PaymentCancelResponseDto.builder()
                    .paymentKey(request.getPaymentKey())
                    .status("ERROR")
                    .message("결제 취소 중 오류 발생: " + e.getMessage())
                    .build();
        }
    }


    @Override
    public PaymentResponseDto getPaymentDetail(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        return PaymentResponseDto.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .approvedAt(payment.getApprovedAt().atOffset(OffsetDateTime.now().getOffset())) // LocalDateTime -> OffsetDateTime
                .selectedQuoteId(payment.getSelectedQuoteId().getSelectedQuoteId())
                .paymentTitle(payment.getPaymentTitle())
                .message("결제 내역 조회 성공")
                .build();
    }

}
