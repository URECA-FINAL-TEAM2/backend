package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.GetMainCustomerResponseDto.MainResponse;

public interface MainService {

    // 고객 메인 페이지 조회
    MainResponse getMainPage(Long customerId);
}
