package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    // 프로필 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDto>> getCustomerProfile(@PathVariable Long customerId) {
        CustomerProfileResponseDto responseDto = customerService.getCustomerProfile(customerId);
        if (responseDto == null) {
            return ApiResponse.ok(404, null, "고객 프로필을 찾을 수 없습니다.");
        }
        return ApiResponse.ok(200, responseDto, "고객 프로필 조회 성공");
    }

    // 프로필 삭제
    @PatchMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomerProfile(@PathVariable Long customerId) {
        customerService.deleteCustomerProfile(customerId);
        return ApiResponse.ok(204, null, "고객 프로필 삭제 성공");
    }
}