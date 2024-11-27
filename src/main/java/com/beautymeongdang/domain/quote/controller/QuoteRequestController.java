package com.beautymeongdang.domain.quote.controller;

import com.beautymeongdang.domain.quote.service.QuoteRequestService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class QuoteRequestController {

    private final QuoteRequestService quoteRequestService;

    // 미용사가 받은 1:1 요청 조회
    @GetMapping("/groomer/{groomerId}")
    public ResponseEntity<?> getGroomerDirectRequestList(@PathVariable(name = "groomerId") Long groomerId) {
        return ApiResponse.ok(200, quoteRequestService.getGroomerDirectRequestList(groomerId), "Get DirectRequestGroomer Success");
    }

}
