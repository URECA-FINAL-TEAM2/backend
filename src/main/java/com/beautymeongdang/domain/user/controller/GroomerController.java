package com.beautymeongdang.domain.user.controller;

import com.beautymeongdang.domain.user.dto.UpdateGroomerPortfolioDto;
import com.beautymeongdang.domain.user.dto.UpdateGroomerProfileDto;
import com.beautymeongdang.domain.user.service.GroomerService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    // 미용사 포트폴리오 수정
    @PutMapping(value = "/portfolio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateGroomerPortfolio(@RequestPart UpdateGroomerPortfolioDto requestDto,
                                                    @RequestPart(required = false) List<MultipartFile> images) {
        return ApiResponse.ok(200, groomerService.updateGroomerPortfolio(requestDto, images), "Update GroomerPortfolio Success");
    }

    // 미용사 프로필 논리적 삭제
    @PutMapping("/delete/{groomerId}")
    public ResponseEntity<?> deleteGroomer(@PathVariable Long groomerId) {
        return ApiResponse.ok(200, groomerService.deleteGroomerProfile(groomerId), "Delete Groomer Success");
    }

    // 미용사 프로필 수정
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateGroomerProfile(@RequestPart UpdateGroomerProfileDto requestDto,
                                                  @RequestPart(required = false) List<MultipartFile> profileImage) {
        return ApiResponse.ok(200, groomerService.updateGroomerProfile(requestDto, profileImage), "Update GroomerProfile Success");
    }


}
