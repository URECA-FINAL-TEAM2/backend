package com.beautymeongdang.domain.shop.service;

import com.beautymeongdang.domain.shop.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopService {

    // 매장 생성
    CreateShopResponseDto createShop(Long groomerId, CreateShopRequestDto requestDto, MultipartFile shopLogo);

    // 매장 상세 조회 (customer)
    GetShopDetailResponseDto getShopDetail(Long shopId, Long customerId);

    // 자기 매장 상세 조회 (groomer)
    GetMyGroomerShopDetailResponseDto getMyShopDetail(Long groomerId);

    // 매장 조회 (미용사 마이 페이지)
    GetShopResponseDto getGroomerShop(Long groomerId);

    // 매장 수정
    UpdateShopResponseDto updateShop(Long shopId, Long groomerId, UpdateShopRequestDto requestDto, MultipartFile shopLogo);

    // 매장 삭제
    DeleteShopResponseDto deleteShop(Long shopId,Long groomerId);

    // 미용사 찾기 매장 리스트 조회
    GetGroomerShopListResponseDto.ShopListResponse getShopList(Long customerId);

    // 매장 찜 삭제
    DeleteFavoriteResponseDto deleteFavorite(Long customerId, Long shopId);

    // 매장 찜 등록
    CreateFavoriteResponseDto createFavorite(CreateFavoriteRequestDto requestDto);

    // 찜한 매장 리스트 조회
    List<GetFavoriteShopListResponseDto> getFavoriteShops(Long customerId);
}