package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.shop.dto.ShopDTO;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegistrationDTO;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Optional<User> findById(Long userId);  // 이미 JpaRepository에 있지만 명시적으로 추가


    User registerCustomer(Long userId, CustomerDTO customerDTO);

    User registerGroomer(Long userId, GroomerRegistrationDTO registrationDTO);
}