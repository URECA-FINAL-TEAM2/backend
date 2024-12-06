package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.CustomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegisterRequestDTO;
import com.beautymeongdang.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@Service
public interface UserService {
    Map<String, Object> registerCustomer(Long userId, Map<String, String> requestDto, MultipartFile profileImage);
    Map<String, Object> registerGroomer(Long userId, Map<String, String> requestDto);
    String getNicknameCheckMessage(String nickname);
    void logout(HttpServletRequest request, HttpServletResponse response);
}