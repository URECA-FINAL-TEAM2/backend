package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    Map<String, Object> registerCustomer(CustomerRegisterRequestDTO requestDto, MultipartFile profileImage);
    Map<String, Object> registerGroomer(GroomerRegisterRequestDTO requestDto, MultipartFile profileImage);
    String getNicknameCheckMessage(String nickname);
    void logout(HttpServletRequest request, HttpServletResponse response);
}