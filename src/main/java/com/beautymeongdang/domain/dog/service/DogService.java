package com.beautymeongdang.domain.dog.service;

import com.beautymeongdang.domain.dog.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface DogService {

    // 반려견 프로필 등록
    CreateDogResponseDto createDog(Long customerId, CreateDogRequestDto requestDto, MultipartFile dogProfile);

    // 반려견 프로필 조회
    GetDogResponseDto getDog(Long dogId, Long customerId);

    // 반려견 프로필 수정
    UpdateDogResponseDto updateDog(Long dogId, Long customerId, UpdateDogRequestDto requestDto, MultipartFile dogProfile);


}