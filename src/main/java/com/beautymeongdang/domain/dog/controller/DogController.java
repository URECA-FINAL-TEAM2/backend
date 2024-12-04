package com.beautymeongdang.domain.dog.controller;


import com.beautymeongdang.domain.dog.dto.CreateDogRequestDto;
import com.beautymeongdang.domain.dog.dto.CreateDogResponseDto;
import com.beautymeongdang.domain.dog.dto.GetDogResponseDto;
import com.beautymeongdang.domain.dog.service.DogService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/profile/customer/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;


    /**
     * 반려견 프로필 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreateDogResponseDto>> createDog(
            @RequestParam Long customerId,
            @RequestPart CreateDogRequestDto requestDto,
            @RequestPart MultipartFile dogProfile) {
        CreateDogResponseDto response = dogService.createDog(customerId, requestDto, dogProfile);
        return ApiResponse.ok(200, response, "반려견 등록 성공");
    }

    /**
     * 반려견 프로필 조회
     */
    @GetMapping("/{dogId}")
    public ResponseEntity<ApiResponse<GetDogResponseDto>> getDog(
            @PathVariable Long dogId,
            @RequestParam Long customerId) {
        GetDogResponseDto response = dogService.getDog(dogId, customerId);
        return ApiResponse.ok(200, response, "반려견 정보 조회 성공");
    }
}