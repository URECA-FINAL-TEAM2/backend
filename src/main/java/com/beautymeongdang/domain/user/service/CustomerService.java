package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.GetCustomerAddressResponseDto;
import com.beautymeongdang.domain.user.dto.UpdateCustomerProfileDto;
import com.beautymeongdang.domain.user.dto.GetCustomerMypageResponseDto;
import com.beautymeongdang.domain.user.repository.DeleteCustomerResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
    // 고객 프로필 조회
    CustomerProfileResponseDto getCustomerProfile(Long customerId);

    // 고객 프로필 삭제
    DeleteCustomerResponseDto deleteCustomerProfile(Long customerId);

    // 고객 프로필 수정
    UpdateCustomerProfileDto updateCustomerProfile(UpdateCustomerProfileDto updateCustomerProfileDto, List<MultipartFile> images);

    // 고객 주소 조회
    GetCustomerAddressResponseDto getCustomerAddress(Long customerId);

    // 고객 주소 수정
    void updateAddress(Long customerId, String sidoName, String sigunguName);

    // 마이페이지 조회
    GetCustomerMypageResponseDto getCustomerMypage(Long customerId);
}