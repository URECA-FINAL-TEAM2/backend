package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    User registerCustomer(Long userId, CustomerRegisterRequestDTO customerDTO);

    User registerGroomer(Long userId, GroomerRegisterRequestDTO registrationDTO);
}