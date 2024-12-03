package com.beautymeongdang.domain.shop.controller;


import com.beautymeongdang.domain.shop.dto.*;
import com.beautymeongdang.domain.shop.service.ShopService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
            @RequestPart CreateShopRequestDto requestDto,
            @RequestPart MultipartFile shopLogo) {
        CreateShopResponseDto response = shopService.createShop(requestDto, shopLogo);
        return ApiResponse.ok(200, response, "매장 등록 성공");
    }


    /**
     * 매장 상세 조회
     */
    @GetMapping("/groomer/shop/detail")
    public ResponseEntity<ApiResponse<GetShopDetailResponseDto>> getShopDetail(
            @RequestParam Long shopId,
            @RequestParam Long customerId) {
        GetShopDetailResponseDto response = shopService.getShopDetail(shopId, customerId);
        return ApiResponse.ok(200, response, "매장 상세 조회 성공");
    }

    /**
     * 매장 삭제
     */
    @DeleteMapping("/groomer/shop/{shopId}")
    public ResponseEntity<ApiResponse<DeleteShopResponseDto>> deleteShop(@PathVariable Long shopId) {
        DeleteShopResponseDto response = shopService.deleteShop(shopId);
        return ApiResponse.ok(200, response, "매장 삭제 성공");
    }


    /**
     * 미용사 찾기 매장 리스트 조회
     */
    @GetMapping("/groomer/shop")
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
            @RequestBody DeleteFavoriteRequestDto requestDto) {
        DeleteFavoriteResponseDto response = shopService.deleteFavorite(
                requestDto.getCustomerId(),
                requestDto.getShopId());
        return ApiResponse.ok(200, response, "매장 찜 삭제 성공하였습니다.");
    }
}
