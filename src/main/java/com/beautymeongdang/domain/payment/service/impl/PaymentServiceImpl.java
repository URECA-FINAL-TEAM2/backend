package com.beautymeongdang.domain.payment.service.impl;

import com.beautymeongdang.domain.payment.dto.PaymentCancelRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentCancelResponseDto;
import com.beautymeongdang.domain.payment.dto.PaymentRequestDto;
import com.beautymeongdang.domain.payment.dto.PaymentResponseDto;
import com.beautymeongdang.domain.payment.entity.Payment;
import com.beautymeongdang.domain.payment.repository.PaymentRepository;
import com.beautymeongdang.domain.payment.service.PaymentService;
import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.QuoteRepository;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${toss.payments.secret.key}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ShopRepository shopRepository;
    private final QuoteRepository quoteRepository;
    private final CustomerRepository customerRepository;
    private final WebClient webClient;

    private static final String TOSS_PAYMENTS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    // 결제 승인 요청 및 예약 완료
    @Override
    @Transactional
    public PaymentResponseDto confirmPayment(PaymentRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        Map<String, Object> body = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri(TOSS_PAYMENTS_CONFIRM_URL)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null && response.get("approvedAt") != null) {
                OffsetDateTime approvedAtOffset = OffsetDateTime.parse(response.get("approvedAt").toString());
                LocalDateTime approvedAt = approvedAtOffset.toLocalDateTime();
                String method = response.get("method").toString();

                SelectedQuote selectedQuote = createSelectedQuote(request.getQuoteId(), request.getCustomerId());


                Long groomerId = selectedQuote.getQuoteId().getGroomerId().getGroomerId();
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

                selectedQuote = selectedQuote.updateStatus("예약 완료");
                selectedQuoteRepository.save(selectedQuote);

                // 6. 응답 반환
                return PaymentResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .orderId(request.getOrderId())
                        .status("결제 완료")
                        .method(method)
                        .approvedAt(approvedAtOffset)
                        .amount(request.getAmount())
                        .selectedQuoteId(selectedQuote.getSelectedQuoteId())
                        .message("결제 승인 성공")
                        .paymentTitle(shopName)
                        .build();
            } else {
                return PaymentResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("FAILED")
                        .message("결제 승인 실패: 응답이 유효하지 않습니다.")
                        .build();
            }
        } catch (Exception e) {
            if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                org.springframework.web.reactive.function.client.WebClientResponseException we =
                        (org.springframework.web.reactive.function.client.WebClientResponseException) e;

                String errorBody = we.getResponseBodyAsString();
                return PaymentResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("ERROR")
                        .message("결제 승인 중 오류 발생: " + we.getStatusCode() + ", 응답 본문: " + errorBody)
                        .build();
            }

            return PaymentResponseDto.builder()
                    .paymentKey(request.getPaymentKey())
                    .status("ERROR")
                    .message("결제 승인 중 예상치 못한 오류 발생: " + e.getMessage())
                    .build();
        }
    }

    // 선택된 견적서 생성
    private SelectedQuote createSelectedQuote(Long quoteId, Long customerId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("견적 데이터를 찾을 수 없습니다."));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("고객 데이터를 찾을 수 없습니다."));

        SelectedQuote selectedQuote = SelectedQuote.builder()
                .quoteId(quote)
                .customerId(customer)
                .status("선택완료")
                .build();

        return selectedQuoteRepository.save(selectedQuote);
    }

    // 결제 취소 및 예약 취소
    @Override
    @Transactional
    public PaymentCancelResponseDto cancelPayment(PaymentCancelRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(secretKey, "");

        Map<String, Object> body = Map.of(
                "cancelReason", request.getCancelReason()
        );

        String url = "https://api.tosspayments.com/v1/payments/" + request.getPaymentKey() + "/cancel";

        try {
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response != null) {
                Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
                        .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

                payment = payment.toBuilder()
                        .status("결제 취소")
                        .cancelReason(request.getCancelReason())
                        .build();

                paymentRepository.save(payment);

                SelectedQuote selectedQuote = payment.getSelectedQuoteId();
                selectedQuote = selectedQuote.updateStatus("예약 취소");
                selectedQuoteRepository.save(selectedQuote);

                return PaymentCancelResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("결제 취소")
                        .method(payment.getMethod())
                        .cancelReason(request.getCancelReason())
                        .selectedQuoteId(payment.getSelectedQuoteId().getSelectedQuoteId())
                        .message("결제 취소 성공")
                        .build();
            } else {
                return PaymentCancelResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("FAILED")
                        .message("결제 취소 실패: 응답이 유효하지 않습니다.")
                        .build();
            }
        } catch (Exception e) {
            if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                org.springframework.web.reactive.function.client.WebClientResponseException we =
                        (org.springframework.web.reactive.function.client.WebClientResponseException) e;

                String errorBody = we.getResponseBodyAsString();
                return PaymentCancelResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status("ERROR")
                        .message("결제 취소 중 오류 발생: " + we.getStatusCode() + ", 응답 본문: " + errorBody)
                        .build();
            }

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
                .cancelReason(payment.getCancelReason())
                .build();
    }

}