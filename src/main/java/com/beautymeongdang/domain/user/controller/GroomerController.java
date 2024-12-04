package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.service.GroomerService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile/groomer")
@RequiredArgsConstructor
public class GroomerController {
    private final GroomerService groomerService;

    // 미용사 정보 조회
    @GetMapping("/{groomerId}")
    public ResponseEntity<?> getGroomer(@PathVariable Long groomerId) {
        return ApiResponse.ok(200, groomerService.getGroomerProfile(groomerId), "Get Groomer Success");
    }

}
