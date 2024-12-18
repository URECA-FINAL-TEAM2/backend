package com.beautymeongdang.global.common.scheduler.payment;

import com.beautymeongdang.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentScheduledService {

    private final PaymentService paymentService;

    // 결제 물리적 삭제
    @Scheduled(cron = "0 30 0 * * *")
    public void deleteExpiredPayments() {
        paymentService.deleteExpiredLogicalDeletedPayments();
    }
}
