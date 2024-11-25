package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.login.dto.CustomOAuth2User;
import com.beautymeongdang.domain.shop.dto.ShopDTO;
import com.beautymeongdang.domain.user.dto.CustomerDTO;
import com.beautymeongdang.domain.user.dto.GroomerDTO;
import com.beautymeongdang.domain.user.dto.UserDTO;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @RequestBody CustomerDTO customerDTO) {

        User user = userService.registerCustomer(oauth2User.getUsername(), customerDTO);
        return ResponseEntity.ok("고객 등록이 완료되었습니다.");
    }

    @PostMapping("/register/groomer")
    public ResponseEntity<?> registerGroomer(
            @AuthenticationPrincipal CustomOAuth2User oauth2User,
            @RequestBody GroomerDTO groomerDTO,
            @RequestBody ShopDTO shopDTO) {

        User user = userService.registerGroomer(oauth2User.getUsername(), groomerDTO, shopDTO);
        return ResponseEntity.ok("미용사 등록이 완료되었습니다.");
    }
    @GetMapping("/user/additional-info")
    public String showAdditionalInfoForm(@AuthenticationPrincipal CustomOAuth2User oauth2User, Model model) {
        UserDTO userDTO = oauth2User.getUserDTO();
        model.addAttribute("user", userDTO);
        return "user/additional-info";
    }
}