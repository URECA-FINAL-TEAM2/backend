package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;

public interface GroomerService {

    // 미용사 정보 조회
    GetGroomerProfileResponseDto getGroomerProfile(Long groomerId);

}
