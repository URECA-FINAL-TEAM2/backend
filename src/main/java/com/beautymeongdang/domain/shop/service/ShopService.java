package com.beautymeongdang.domain.shop.service;

import com.beautymeongdang.domain.shop.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface ShopService {

    // 매장 생성
    CreateShopResponseDto createShop(CreateShopRequestDto requestDto, MultipartFile shopLogo);

    // 매장 상세 조회
    GetShopDetailResponseDto.ShopDetailResponseDto getShopDetail(Long groomerId, Long customerId);

    // 매장 삭제
    DeleteShopResponseDto deleteShop(Long shopId);

    // 미용사 찾기 매장 리스트 조회
    GetGroomerShopListResponseDto.ShopListResponse getShopList(Long customerId);


}