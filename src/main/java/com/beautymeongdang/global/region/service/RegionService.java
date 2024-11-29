package com.beautymeongdang.global.region.service;

import com.beautymeongdang.global.region.dto.GetSidoResponseDto;
import com.beautymeongdang.global.region.dto.GetSigunguResponseDto;

public interface RegionService {

    // 시도 조회
    GetSidoResponseDto getSidoList();

    // 시군구 조회
    GetSigunguResponseDto getSigunguList(Long sidoId);
}
