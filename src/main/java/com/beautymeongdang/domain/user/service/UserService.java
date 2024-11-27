package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.shop.dto.ShopDTO;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegistrationDTO;
import com.beautymeongdang.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User registerCustomer(String username, CustomerDTO customerDTO);

    User registerGroomer(String username, GroomerRegistrationDTO registrationDTO);


}