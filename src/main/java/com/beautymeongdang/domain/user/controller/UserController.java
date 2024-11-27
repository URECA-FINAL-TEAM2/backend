package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.domain.shop.dto.ShopDTO;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerRegistrationDTO;
import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.service.UserService;
import com.beautymeongdang.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.TokenService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;


    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @RequestBody CustomerDTO customerDTO,
            HttpServletResponse response) {

        User user = userService.registerCustomer(oauth2User.getUserId(), customerDTO);
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);

        return ResponseEntity.ok(tokenInfo);
    }

    @PostMapping("/register/groomer")
    public ResponseEntity<?> registerGroomer(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @RequestBody GroomerRegistrationDTO registrationDTO,
            HttpServletResponse response) {

        User user = userService.registerGroomer(oauth2User.getUserId(), registrationDTO);
        Map<String, Object> tokenInfo = jwtProvider.createTokens(user, response);

        return ResponseEntity.ok(tokenInfo);
    }

}