package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.GetGroomerMypageResponseDto;

public interface MypageService {

    // 미용사 마이페이지 조회
    GetGroomerMypageResponseDto getGroomerMypage(Long groomerId);

}
