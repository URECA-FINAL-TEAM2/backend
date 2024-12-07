package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.GetCustomerMypageResponseDto;
import com.beautymeongdang.domain.user.dto.UpdateAddressRequestDto;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
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
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomerProfile(@PathVariable Long customerId) {
        customerService.deleteCustomerProfile(customerId);
        return ApiResponse.ok(204, null, "고객 프로필 삭제 성공");
    }

    // 고객 주소 수정
    @PutMapping("/{customerId}/address")
    public ResponseEntity<ApiResponse<Void>> updateAddress(
            @PathVariable Long customerId,
            @RequestBody UpdateAddressRequestDto request) {
        customerService.updateAddress(customerId, request.getSidoName(), request.getSigunguName());
        return ApiResponse.ok(200, null, "고객 주소 업데이트 성공");
    }
    
    // 마이페이지 조회 API 추가
    @GetMapping("/{customerId}/mypage")
    public ResponseEntity<ApiResponse<GetCustomerMypageResponseDto>> getCustomerMypage(@PathVariable Long customerId) {
        try {
            GetCustomerMypageResponseDto responseDto = customerService.getCustomerMypage(customerId);
            return ApiResponse.ok(200, responseDto, "Get Customer MyPage Success");
        } catch (EntityNotFoundException e) {
            return ApiResponse.badRequest(404, "고객을 찾을 수 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "마이페이지 조회 실패: " + e.getMessage());
        }
    }
}