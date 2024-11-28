package com.beautymeongdang.domain.shop.service;

import com.beautymeongdang.domain.shop.dto.GetShopDetailResponseDto;

public interface ShopService {
    GetShopDetailResponseDto.ShopDetailResponseDto getShopDetail(Long groomerId, Long customerId);
}