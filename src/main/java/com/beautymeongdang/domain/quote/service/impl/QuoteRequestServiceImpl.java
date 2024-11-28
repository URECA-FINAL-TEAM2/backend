package com.beautymeongdang.domain.quote.service.impl;


import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestAllResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestAllRequestDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestGroomerResponseDto;
import com.beautymeongdang.domain.quote.dto.CreateInsertRequestGroomerRequestDto;
import com.beautymeongdang.domain.quote.entity.*;
import com.beautymeongdang.domain.quote.repository.DirectQuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestImageRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.TotalQuoteRequestRepository;
import com.beautymeongdang.domain.quote.service.QuoteRequestService;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuoteRequestServiceImpl implements QuoteRequestService {
    private final QuoteRequestRepository quoteRequestRepository;
    private final DogRepository dogRepository;
    private final GroomerRepository groomerRepository;
    private final DirectQuoteRequestRepository directQuoteRequestRepository;
    private final TotalQuoteRequestRepository totalQuoteRequestRepository;
    private final SigunguRepository sigunguRepository;
    private final QuoteRequestImageRepository quoteRequestImageRepository;
    private final FileStore fileStore;

    /**
     전체 견적서 요청하기
     */
    @Override
    @Transactional
    public CreateInsertRequestAllResponseDto createInsertRequestAll(CreateInsertRequestAllRequestDto requestDto,List<MultipartFile> images) {
        Dog dog = dogRepository.findById(requestDto.getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        Sigungu sigungu = sigunguRepository.findById(requestDto.getSigunguId())
                .orElseThrow(() -> NotFoundException.entityNotFound("지역정보"));

        QuoteRequest quoteRequest = QuoteRequest.builder()
                .dogId(dog)
                .content(requestDto.getRequestContent())
                .beautyDate(requestDto.getBeautyDate())
                .requestType(requestDto.getRequestType())
                .status(requestDto.getStatus())
                .build();

        QuoteRequest savedRequest = quoteRequestRepository.save(quoteRequest);

        TotalQuoteRequest totalQuoteRequest = TotalQuoteRequest.builder()
                .requestId(savedRequest)
                .sigunguId(sigungu)
                .build();

        totalQuoteRequestRepository.save(totalQuoteRequest);

        // 이미지 저장
        List<QuoteRequestImage> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.QUOTE_REQUEST);

            List<QuoteRequestImage> quoteImages = uploadedFiles.stream()
                    .map(uploadedFile -> QuoteRequestImage.builder()
                            .requestId(savedRequest)
                            .imageUrl(uploadedFile.getFileUrl())
                            .build())
                    .collect(Collectors.toList());

            savedImages = quoteRequestImageRepository.saveAll(quoteImages);
        }


        return CreateInsertRequestAllResponseDto.builder()
                .requestId(savedRequest.getRequestId())
                .dogId(savedRequest.getDogId().getDogId())
                .requestType(savedRequest.getRequestType())
                .requestContent(savedRequest.getContent())
                .beautyDate(savedRequest.getBeautyDate())
                .status(savedRequest.getStatus())
                .sigunguId(sigungu.getSigunguId())
                .quoteRequestImage(savedImages.stream()
                        .map(QuoteRequestImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     1:1 견적서 요청하기
     */
    @Override
    @Transactional
    public CreateInsertRequestGroomerResponseDto createInsertRequestGroomer(CreateInsertRequestGroomerRequestDto requestDto,List<MultipartFile> images) {

        Dog dog = dogRepository.findById(requestDto.getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        Groomer groomer = groomerRepository.findById(requestDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        QuoteRequest quoteRequest = QuoteRequest.builder()
                .dogId(dog)
                .content(requestDto.getRequestContent())
                .beautyDate(requestDto.getBeautyDate())
                .requestType(requestDto.getRequestType())
                .status(requestDto.getStatus())
                .build();

        QuoteRequest savedRequest = quoteRequestRepository.save(quoteRequest);

        DirectQuoteRequestId directQuoteRequestId = new DirectQuoteRequestId(savedRequest, groomer);
        DirectQuoteRequest directQuoteRequest = DirectQuoteRequest.builder()
                .directQuoteRequestId(directQuoteRequestId)
                .build();

        directQuoteRequestRepository.save(directQuoteRequest);

        // 이미지 저장
        List<QuoteRequestImage> savedImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            List<UploadedFile> uploadedFiles = fileStore.storeFiles(images, FileStore.QUOTE_REQUEST);

            List<QuoteRequestImage> quoteImages = uploadedFiles.stream()
                    .map(uploadedFile -> QuoteRequestImage.builder()
                            .requestId(savedRequest)
                            .imageUrl(uploadedFile.getFileUrl())
                            .build())
                    .collect(Collectors.toList());

            savedImages = quoteRequestImageRepository.saveAll(quoteImages);
        }

        return CreateInsertRequestGroomerResponseDto.builder()
                .requestId(savedRequest.getRequestId())
                .dogId(savedRequest.getDogId().getDogId())
                .groomerId(groomer.getGroomerId())
                .requestType(savedRequest.getRequestType())
                .requestContent(savedRequest.getContent())
                .beautyDate(savedRequest.getBeautyDate())
                .status(savedRequest.getStatus())
                .quoteRequestImage(savedImages.stream()
                        .map(QuoteRequestImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();

    }

    // 미용사가 받은 1:1 요청 조회
    @Override
    public List<GetGroomerQuoteRequestResponseDto> getGroomerDirectRequestList (Long groomerId){
        return quoteRequestRepository.findQuoteRequestsByGroomerId(groomerId);
    }

    // 미용사 매장 근처 견적서 요청 공고 조회
    @Override
    public List<GetGroomerQuoteRequestResponseDto> getGroomerTotalRequestList(Long sigunguId) {
        return quoteRequestRepository.findQuoteRequestsBySigunguId(sigunguId);
    }
}
