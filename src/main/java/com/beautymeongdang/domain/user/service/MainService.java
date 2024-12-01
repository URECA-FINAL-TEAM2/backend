package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.GetMainCustomerResponseDto.MainResponse;
import com.beautymeongdang.domain.user.dto.GetMainGroomerResponseDto;

public interface MainService {

    // 고객 메인 페이지 조회
    MainResponse getMainPage(Long customerId);

    // 미용사 메인 페이지 조회
    GetMainGroomerResponseDto getMainGroomerPage(Long groomerId);
}
