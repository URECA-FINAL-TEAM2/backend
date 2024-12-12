package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.GetCustomerMypageResponseDto;
import com.beautymeongdang.domain.user.dto.GetGroomerMypageResponseDto;

public interface MypageService {

    // 미용사 마이페이지 조회
    GetGroomerMypageResponseDto getGroomerMypage(Long groomerId);

    // 마이페이지 조회
    GetCustomerMypageResponseDto getCustomerMypage(Long customerId);

    // 고객 토글
    Long getCustomerIdByUserId(Long userId);
    
    // 미용사 토글
    Long getGroomerIdByUserId(Long userId);
}
