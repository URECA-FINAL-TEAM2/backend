package com.beautymeongdang.global.region.controller;

import com.beautymeongdang.global.region.dto.SigunguDto;
import com.beautymeongdang.global.region.service.SigunguService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SigunguController {
    private final SigunguService sigunguService;

    // 이거 어따 쓰는거지??
    @GetMapping("/sigungu")
    public ResponseEntity<List<SigunguDto>> getSigunguList(@RequestParam Long sidoId) {
        List<SigunguDto> sigunguList = sigunguService.getSigunguList(sidoId);
        return ResponseEntity.ok(sigunguList);
    }
}