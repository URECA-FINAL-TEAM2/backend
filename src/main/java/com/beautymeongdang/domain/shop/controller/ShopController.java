package com.beautymeongdang.domain.shop.controller;


import com.beautymeongdang.domain.shop.dto.*;
import com.beautymeongdang.domain.shop.service.ShopService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    /**
     * 매장 등록
     */
    @PostMapping(value = "/groomer/shop", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateShopResponseDto>> createShop(
            @RequestParam Long groomerId,
            @RequestPart CreateShopRequestDto requestDto,
            @RequestPart MultipartFile shopLogo) {
        CreateShopResponseDto response = shopService.createShop(groomerId, requestDto, shopLogo);
        return ApiResponse.ok(200, response, "매장 등록 성공");
    }


    /**
     * 매장 조회 (미용사 마이 페이지)
     */
    @GetMapping("/groomer/shop")
    public ResponseEntity<ApiResponse<GetShopResponseDto>> getGroomerShop(
            @RequestParam Long groomerId) {
        GetShopResponseDto response = shopService.getGroomerShop(groomerId);
        return ApiResponse.ok(200, response, "매장 조회 성공");
    }


    /**
     * 매장 수정
     */
    @PutMapping(value = "/groomer/shop/{shopId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UpdateShopResponseDto>> updateShop(
            @PathVariable Long shopId,
            @RequestParam Long groomerId,
            @RequestPart UpdateShopRequestDto requestDto,
            @RequestPart(required = false) MultipartFile shopLogo) {
        UpdateShopResponseDto response = shopService.updateShop(shopId, groomerId, requestDto, shopLogo);
        return ApiResponse.ok(200, response, "매장 수정 성공");
    }



    /**
     * 매장 상세 조회 (customer)
     */
    @GetMapping("/groomer/shop/detail/customer")
    public ResponseEntity<ApiResponse<GetShopDetailResponseDto>> getShopDetail(
            @RequestParam Long shopId,
            @RequestParam Long customerId) {
        GetShopDetailResponseDto response = shopService.getShopDetail(shopId, customerId);
        return ApiResponse.ok(200, response, "매장 상세 조회 성공");
    }


    /**
     * 자기 매장 상세 조회 (groomer)
     */
    @GetMapping("/groomer/shop/detail/groomer")
    public ResponseEntity<ApiResponse<GetMyGroomerShopDetailResponseDto>> getMyShopDetail(
            @RequestParam Long groomerId) {
        GetMyGroomerShopDetailResponseDto response = shopService.getMyShopDetail(groomerId);
        return ApiResponse.ok(200, response, "내 매장 상세 조회 성공");
    }


    /**
     * 매장 논리적 삭제
     */
    @PutMapping("/groomer/shop/{shopId}/delete")
    public ResponseEntity<ApiResponse<DeleteShopResponseDto>> deleteShop(
            @PathVariable Long shopId,
            @RequestParam Long groomerId) {
        DeleteShopResponseDto response = shopService.deleteShop(shopId, groomerId);
        return ApiResponse.ok(200, response, "매장 삭제 성공");
    }


    /**
     * 미용사 찾기 매장 리스트 조회
     */
    @GetMapping("/groomer/shop/list")
    public ResponseEntity<ApiResponse<GetGroomerShopListResponseDto.ShopListResponse>> getGroomerShopList(
            @RequestParam Long customerId) {
        GetGroomerShopListResponseDto.ShopListResponse response = shopService.getShopList(customerId);
        return ApiResponse.ok(200, response, "매장 목록 조회 성공");
    }

    // 매장 찜 등록
    @PostMapping("/groomer/shop/favorite")
    public ResponseEntity<?> createFavoriteShop(@RequestBody CreateFavoriteRequestDto requestDto) {
        return ApiResponse.ok(200, shopService.createFavorite(requestDto), "매장 추천 성공하였습니다.");
    }

    /**
     * 매장 찜 삭제
     */
    @DeleteMapping("/groomer/shop/favorite")
    public ResponseEntity<ApiResponse<DeleteFavoriteResponseDto>> deleteFavorite(
            @RequestParam Long customerId,
            @RequestParam Long shopId) {
        DeleteFavoriteResponseDto response = shopService.deleteFavorite(customerId, shopId);
        return ApiResponse.ok(200, response, "매장 찜 삭제 성공하였습니다.");
    }

    // 찜한 매장 리스트 조회
    @GetMapping("/{customerId}/favorites")
    public ResponseEntity<ApiResponse<List<GetFavoriteShopListResponseDto>>> getFavoriteShops(@PathVariable Long customerId) {
        List<GetFavoriteShopListResponseDto> favoriteShops = shopService.getFavoriteShops(customerId);
        return ApiResponse.ok(200, favoriteShops, "찜한 매장 리스트 조회 성공");
    }

}
