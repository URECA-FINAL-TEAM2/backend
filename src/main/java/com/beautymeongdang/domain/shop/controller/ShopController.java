package com.beautymeongdang.domain.shop.controller;


import com.beautymeongdang.domain.shop.dto.GetShopDetailResponseDto;
import com.beautymeongdang.domain.shop.service.ShopService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    /**
     * 매장 상세 조회
     */
    @GetMapping("/groomer/shop/detail")
    public ResponseEntity<ApiResponse<GetShopDetailResponseDto.ShopDetailResponseDto>> getShopDetail(
            @RequestParam Long groomerId,
            @RequestParam(required = false) Long customerId) {
        GetShopDetailResponseDto.ShopDetailResponseDto response = shopService.getShopDetail(groomerId, customerId);
        return ApiResponse.ok(200, response, "매장 상세 조회 성공");
    }


}
