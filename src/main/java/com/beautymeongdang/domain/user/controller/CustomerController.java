package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.dto.*;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/profile/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    // 고객 프로필 조회
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDto>> getCustomerProfile(@PathVariable Long customerId) {
        CustomerProfileResponseDto responseDto = customerService.getCustomerProfile(customerId);
        if (responseDto == null) {
            return ApiResponse.ok(404, null, "고객 프로필을 찾을 수 없습니다.");
        }
        return ApiResponse.ok(200, responseDto, "고객 프로필 조회 성공");
    }

    // 고객 프로필 삭제
    @PutMapping("/delete/{customerId}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomerProfile(@PathVariable Long customerId) {
        customerService.deleteCustomerProfile(customerId);
        return ApiResponse.ok(204, null, "고객 프로필 삭제 성공");
    }

    // 고객 프로필 수정
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UpdateCustomerProfileDto>> updateCustomerProfile(
            @RequestPart UpdateCustomerProfileDto requestDto,
            @RequestPart(required = false) List<MultipartFile> profileImage) {
        UpdateCustomerProfileDto updatedProfile = customerService.updateCustomerProfile(requestDto, profileImage);
        return ApiResponse.ok(200, updatedProfile, "고객 프로필 수정 성공");
    }

    // 고객 주소 조회
    @GetMapping("/{customerId}/address")
    public ResponseEntity<ApiResponse<GetCustomerAddressResponseDto>> getCustomerAddress(@PathVariable Long customerId) {
        GetCustomerAddressResponseDto responseDto = customerService.getCustomerAddress(customerId);
        if (responseDto == null) {
            return ApiResponse.ok(404, null, "고객 주소를 찾을 수 없습니다.");
        }
        return ApiResponse.ok(200, responseDto, "고객 주소 조회 성공");
    }

    // 고객 주소 수정
    @PutMapping("/{customerId}/address")
    public ResponseEntity<ApiResponse<Void>> updateAddress(
            @PathVariable Long customerId,
            @RequestBody UpdateAddressRequestDto request) {
        customerService.updateAddress(customerId, request.getSidoName(), request.getSigunguName());
        return ApiResponse.ok(200, null, "고객 주소 업데이트 성공");
    }

}