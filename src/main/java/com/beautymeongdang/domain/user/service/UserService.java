package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    Map<String, Object> registerCustomer(Long userId, CustomerRegisterRequestDTO requestDto);
    Map<String, Object> registerGroomer(Long userId, GroomerRegisterRequestDTO requestDto);
    String getNicknameCheckMessage(String nickname);
    void logout(HttpServletRequest request, HttpServletResponse response);
}