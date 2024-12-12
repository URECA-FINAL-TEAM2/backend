package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.GetCustomerMypageResponseDto;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.domain.user.service.MypageService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    // 미용사 마이페이지 조회
    @GetMapping("/groomer/{groomerId}")
    public ResponseEntity<?> getGroomerMypage(@PathVariable Long groomerId) {
        return ApiResponse.ok(200, mypageService.getGroomerMypage(groomerId), "Get Goomer MyPage Success");
    }

    // 고객 마이페이지 조회
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<GetCustomerMypageResponseDto>> getCustomerMypage(@PathVariable Long customerId) {
        try {
            GetCustomerMypageResponseDto responseDto = mypageService.getCustomerMypage(customerId);
            return ApiResponse.ok(200, responseDto, "Get Customer MyPage Success");
        } catch (EntityNotFoundException e) {
            return ApiResponse.badRequest(404, "고객을 찾을 수 없습니다: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "마이페이지 조회 실패: " + e.getMessage());
        }
    }

    @GetMapping("/customer/toggle/{userId}")
    public ResponseEntity<?> getCustomerIdByUserId(@PathVariable Long userId) {
        try {
            Long customerId = mypageService.getCustomerIdByUserId(userId);
            return ApiResponse.ok(200, customerId, "Get Customer ID Success");
        } catch (EntityNotFoundException e) {
            return ApiResponse.badRequest(404, "새로 등록이 필요합니다.");
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "조회 실패: " + e.getMessage());
        }
    }

    @GetMapping("/groomer/toggle/{userId}")
    public ResponseEntity<?> getGroomerIdByUserId(@PathVariable Long userId) {
        try {
            Long groomerId = mypageService.getGroomerIdByUserId(userId);
            return ApiResponse.ok(200, groomerId, "Get Groomer ID Success");
        } catch (EntityNotFoundException e) {
            return ApiResponse.badRequest(404, "새로 등록이 필요합니다.");
        } catch (Exception e) {
            return ApiResponse.badRequest(400, "조회 실패: " + e.getMessage());
        }
    }
}
