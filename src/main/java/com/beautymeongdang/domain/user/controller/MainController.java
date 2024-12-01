package com.beautymeongdang.domain.user.controller;


import com.beautymeongdang.domain.user.dto.GetMainCustomerResponseDto;
import com.beautymeongdang.domain.user.service.MainService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    /**
     * 고객 메인페이지 조회
     */
    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<GetMainCustomerResponseDto.MainResponse>> getMainPage(
            @RequestParam Long customerId) {
        GetMainCustomerResponseDto.MainResponse response = mainService.getMainPage(customerId);
        return ApiResponse.ok(200, response, "메인페이지 조회 성공");
    }

    // 미용사 메인 페이지 조회
    @GetMapping("/groomer/{groomerId}")
    public ResponseEntity<?> getMainGroomerPage(@PathVariable Long groomerId) {
        return ApiResponse.ok(200, mainService.getMainGroomerPage(groomerId), "Get MainGroomer Success");
    }

}