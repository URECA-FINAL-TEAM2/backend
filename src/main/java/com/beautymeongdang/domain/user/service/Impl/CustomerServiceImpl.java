package com.beautymeongdang.domain.user.service.impl;

import com.beautymeongdang.domain.user.dto.CustomerProfileResponseDto;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.service.CustomerService;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final SigunguRepository sigunguRepository;

    @Override
    @Transactional
    public CustomerProfileResponseDto getCustomerProfile(Long customerId) {
        return customerRepository.findCustomerProfileById(customerId);
    }

    @Override
    public void deleteCustomerProfile(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("고객 ID를 찾을 수 없습니다: " + customerId));

        customer.delete();
        customerRepository.save(customer);
    }

    @Transactional
    @Override
    public void updateAddress(Long customerId, String sidoName, String sigunguName) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("고객을 찾을 수 없습니다."));

        Sigungu sigungu = sigunguRepository.findBySidoId_SidoNameAndSigunguName(sidoName, sigunguName)
                .orElseThrow(() -> new EntityNotFoundException("시군구를 찾을 수 없습니다."));

        customer.updateSigungu(sigungu);
    }
}
