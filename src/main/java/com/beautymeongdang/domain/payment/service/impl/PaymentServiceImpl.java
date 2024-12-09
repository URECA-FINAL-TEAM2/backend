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
import com.beautymeongdang.global.common.entity.CommonCode;
import com.beautymeongdang.global.common.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final CommonCodeRepository commonCodeRepository;

    private static final String TOSS_PAYMENTS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    public static final String RESERVATION_GROUP = "250"; // 예약 상태 그룹
    public static final String PAYMENT_GROUP = "300"; // 결제 상태 그룹

    public static final String PAYMENT_COMPLETED = "020";    // 결제 완료
    public static final String PAYMENT_CANCELLED = "030";   // 결제 취소
    public static final String PAYMENT_FAILED = "040";      // 결제 실패
    public static final String PAYMENT_CANCEL_FAILED = "050"; // 결제 취소 실패

    public static final String RESERVATION_COMPLETED = "010"; // 예약 완료
    public static final String RESERVATION_CANCELLED = "020"; // 예약 취소
    public static final String RESERVATION_COMPLETED_GROOMING = "030"; // 미용 완료

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


                Quote quote = quoteRepository.findById(request.getQuoteId())
                        .orElseThrow(() -> new IllegalArgumentException("견적 데이터를 찾을 수 없습니다."));
                Customer customer = customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new IllegalArgumentException("고객 데이터를 찾을 수 없습니다."));

                Long groomerId = quote.getGroomerId().getGroomerId();
                String shopName = shopRepository.findByGroomerId(groomerId)
                        .orElseThrow(() -> new IllegalArgumentException("샵 정보를 찾을 수 없습니다."))
                        .getShopName();

                SelectedQuote selectedQuote = SelectedQuote.builder()
                        .quoteId(quote)
                        .customerId(customer)
                        .status(RESERVATION_COMPLETED)
                        .build();

                selectedQuote = selectedQuoteRepository.save(selectedQuote);

                Payment payment = Payment.builder()
                        .paymentKey(request.getPaymentKey())
                        .orderId(request.getOrderId())
                        .amount(request.getAmount())
                        .method(method)
                        .status(PAYMENT_COMPLETED)
                        .approvedAt(approvedAt)
                        .paymentTitle(shopName)
                        .selectedQuoteId(selectedQuote)
                        .build();

                paymentRepository.save(payment);

                String statusName = commonCodeRepository.findByCodeAndGroupCode(payment.getStatus(), PAYMENT_GROUP)
                        .map(CommonCode::getCommonName)
                        .orElse("알 수 없는 상태");

                return PaymentResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .orderId(request.getOrderId())
                        .status(statusName)
                        .method(method)
                        .approvedAt(approvedAtOffset)
                        .amount(request.getAmount())
                        .selectedQuoteId(selectedQuote.getSelectedQuoteId())
                        .message("결제 승인 성공")
                        .paymentTitle(shopName)
                        .build();
            } else {
                throw new IllegalArgumentException("결제 승인 실패: 응답이 유효하지 않습니다.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("결제 승인 중 오류 발생: " + e.getMessage(), e);
        }
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
                        .status(PAYMENT_CANCELLED)
                        .cancelReason(request.getCancelReason())
                        .build();

                paymentRepository.save(payment);

                SelectedQuote selectedQuote = payment.getSelectedQuoteId();
                selectedQuote = selectedQuote.updateStatus(RESERVATION_CANCELLED);
                selectedQuoteRepository.save(selectedQuote);

                String statusName = commonCodeRepository.findByCodeAndGroupCode(payment.getStatus(), PAYMENT_GROUP)
                        .map(CommonCode::getCommonName)
                        .orElse("알 수 없는 상태");

                return PaymentCancelResponseDto.builder()
                        .paymentKey(request.getPaymentKey())
                        .status(statusName)
                        .method(payment.getMethod())
                        .cancelReason(request.getCancelReason())
                        .selectedQuoteId(payment.getSelectedQuoteId().getSelectedQuoteId())
                        .message("결제 취소 성공")
                        .build();
            } else {
                throw new IllegalArgumentException("결제 취소 실패: 응답이 유효하지 않습니다.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("결제 취소 중 오류 발생: " + e.getMessage(), e);
        }
    }


    // 결제 내역 조회
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentDetail(String paymentKey) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        String statusName = commonCodeRepository.findByCodeAndGroupCode(payment.getStatus(), PAYMENT_GROUP)
                .map(CommonCode::getCommonName)
                .orElse("알 수 없는 상태");

        return PaymentResponseDto.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(statusName)
                .method(payment.getMethod())
                .approvedAt(payment.getApprovedAt().atOffset(OffsetDateTime.now().getOffset())) // LocalDateTime -> OffsetDateTime
                .selectedQuoteId(payment.getSelectedQuoteId().getSelectedQuoteId())
                .paymentTitle(payment.getPaymentTitle())
                .message("결제 내역 조회 성공")
                .cancelReason(payment.getCancelReason())
                .build();
    }

}