package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.user.dto.UpdateGroomerPortfolioDto;
import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.GroomerPortfolioImage;
import com.beautymeongdang.domain.user.repository.GroomerPortfolioImageRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.service.GroomerService;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroomerServiceImpl implements GroomerService {
    private final GroomerRepository groomerRepository;
    private final GroomerPortfolioImageRepository groomerPortfolioImageRepository;
    private final FileStore fileStore;

    // 미용사 정보 조회
    @Override
    public GetGroomerProfileResponseDto getGroomerProfile(Long groomerId) {
        return groomerRepository.findGroomerInfoById(groomerId);
    }

    // 미용사 포트폴리오 수정
    @Override
    @Transactional
    public UpdateGroomerPortfolioDto updateGroomerPortfolio(UpdateGroomerPortfolioDto updateGroomerPortfolioDto, List<MultipartFile> images) {
        if(images!= null && images.size() > 9) {
            throw new BadRequestException("등록 가능한 포트폴리오 이미지 수를 초과하였습니다.");
        }

        Groomer groomer = groomerRepository.findById(updateGroomerPortfolioDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        List<GroomerPortfolioImage> groomerPortfolioImages = groomerPortfolioImageRepository.findAllByGroomerId(groomer);

        // 이미지 S3 삭제
        for (GroomerPortfolioImage groomerImage: groomerPortfolioImages) {
            fileStore.deleteFile(groomerImage.getImageUrl());
        }

        // 이미지 DB 삭제
        groomerPortfolioImageRepository.deleteAllByGroomerId(groomer);

        // 이미지 저장
        List<GroomerPortfolioImage> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.GROOMER_PORTFOLIO);

            List<GroomerPortfolioImage> groomerPortfolioImageList = uploadedFiles.stream()
                    .map(uploadedFile -> GroomerPortfolioImage.builder()
                            .groomerId(groomer)
                            .imageUrl(uploadedFile.getFileUrl())
                            .build())
                    .collect(Collectors.toList());

            savedImages = groomerPortfolioImageRepository.saveAll(groomerPortfolioImageList);
        }

        return UpdateGroomerPortfolioDto.builder()
                .groomerId(groomer.getGroomerId())
                .images(savedImages.stream()
                        .map(GroomerPortfolioImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
