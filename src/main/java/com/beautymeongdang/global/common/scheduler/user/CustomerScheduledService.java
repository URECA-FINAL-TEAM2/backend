package com.beautymeongdang.global.common.scheduler.user;

import com.beautymeongdang.domain.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerScheduledService {
    private final CustomerService customerService;

    // 고객 프로필 물리적 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredCustomers() {
        customerService.deleteExpiredLogicalDeletedCustomers();
    }
}
