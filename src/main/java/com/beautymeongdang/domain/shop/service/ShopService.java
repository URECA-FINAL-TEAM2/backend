package com.beautymeongdang.domain.shop.service;

import com.beautymeongdang.domain.shop.dto.GetShopDetailResponseDto;

public interface ShopService {

    // 매장 상세 조회
    GetShopDetailResponseDto.ShopDetailResponseDto getShopDetail(Long groomerId, Long customerId);
}