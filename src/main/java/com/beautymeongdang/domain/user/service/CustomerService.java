package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;

public interface CustomerService {
    // 고객 프로필 조회
    CustomerProfileResponseDto getCustomerProfile(Long customerId);

    // 고객 프로필 삭제
    void deleteCustomerProfile(Long customerId);

    // 고객 주소 수정
    void updateAddress(Long customerId, String sidoName, String sigunguName);
}