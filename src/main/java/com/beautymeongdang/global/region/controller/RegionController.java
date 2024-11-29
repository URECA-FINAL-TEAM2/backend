package com.beautymeongdang.global.region.controller;

import com.beautymeongdang.global.common.dto.ApiResponse;
import com.beautymeongdang.global.region.dto.GetSidoResponseDto;
import com.beautymeongdang.global.region.dto.GetSigunguResponseDto;
import com.beautymeongdang.global.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 시도 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<GetSidoResponseDto>> getSidoList() {
        GetSidoResponseDto response = regionService.getSidoList();
        return ApiResponse.ok(200, response, "시도 조회 성공");
    }


    /**
     * 시군구 조회
     */
    @GetMapping("/{sidoId}/sigungu")
    public ResponseEntity<ApiResponse<GetSigunguResponseDto>> getSigunguList(
            @PathVariable Long sidoId) {
        GetSigunguResponseDto response = regionService.getSigunguList(sidoId);
        return ApiResponse.ok(200, response, "Get Sigungu Success");
    }

}