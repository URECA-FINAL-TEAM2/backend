package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.service.MypageService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    // 미용사 마이페이지 조회
    @GetMapping("/groomer/{groomerId}")
    public ResponseEntity<?> getGroomerMypage(@PathVariable Long groomerId) {
        return ApiResponse.ok(200, mypageService.getGroomerMypage(groomerId), "Get Goomer MyPage Success");
    }


}
