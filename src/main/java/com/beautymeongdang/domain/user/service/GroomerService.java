package com.beautymeongdang.domain.user.service;

import com.beautymeongdang.domain.user.dto.DeleteGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.dto.UpdateGroomerPortfolioDto;
import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroomerService {

    // 미용사 정보 조회
    GetGroomerProfileResponseDto getGroomerProfile(Long groomerId);

    // 미용사 포트폴리오 수정
    UpdateGroomerPortfolioDto updateGroomerPortfolio(UpdateGroomerPortfolioDto updateGroomerPortfolioDto, List<MultipartFile> images);

    // 미용사 프로필 논리적 삭제
    DeleteGroomerProfileResponseDto deleteGroomerProfile(Long groomerId);

}
