package com.beautymeongdang.domain.quote.service.impl;


import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.quote.dto.*;
import com.beautymeongdang.domain.quote.entity.*;
import com.beautymeongdang.domain.quote.repository.DirectQuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestImageRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.TotalQuoteRequestRepository;
import com.beautymeongdang.domain.quote.service.QuoteRequestService;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.repository.UserRepository;
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
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;


    /**
     * 전체 견적서 요청하기
     */
    @Override
    @Transactional
    public CreateInsertRequestAllResponseDto createInsertRequestAll(Long customerId,CreateInsertRequestAllRequestDto requestDto,List<MultipartFile> images) {
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
     * 1:1 견적서 요청하기
     */
    @Override
    @Transactional
    public CreateInsertRequestGroomerResponseDto createInsertRequestGroomer(Long customerId, CreateInsertRequestGroomerRequestDto requestDto,List<MultipartFile> images) {

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


    /**
     * 고객의 반려견 리스트 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<GetDogListResponseDto> getDogList(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        List<Dog> dogs = dogRepository.findByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(customer);

        return dogs.stream()
                .map(GetDogListResponseDto::of)
                .collect(Collectors.toList());
    }



    /**
     * 고객이 선택한 반려견 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public GetDogInfoResponseDto getDogInfo(Long dogId, Long customerId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        return GetDogInfoResponseDto.builder()
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
                .image(dog.getProfileImage())
                .dogWeight(dog.getDogWeight())
                .dogAge(dog.getDogAge())
                .dogGender(dog.getDogGender().name())
                .neutering(dog.getNeutering())
                .experience(dog.getExperience())
                .significant(dog.getSignificant())
                .build();
    }

    /**
     * 1:1 견적서 요청에서 미용사와 매장 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public GetRequestGroomerShopResponseDto getGroomerShopInfo(Long groomerId) {

        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        return GetRequestGroomerShopResponseDto.builder()
                .shopImage(shop.getImageUrl())
                .groomerName(shop.getGroomerId().getUserId().getUserName())
                .shopName(shop.getShopName())
                .address(shop.getAddress())
                .phone(shop.getGroomerId().getUserId().getPhone())
                .build();
    }


    // 미용사가 받은 1:1 요청 조회
    @Override
    public List<GetGroomerQuoteRequestResponseDto> getGroomerDirectRequestList (Long groomerId){
        return quoteRequestRepository.findQuoteRequestsByGroomerId(groomerId);
    }

    // 미용사 매장 근처 견적서 요청 공고 조회
    @Override
    public List<GetGroomerQuoteRequestResponseDto> getGroomerTotalRequestList(Long groomerId) {
        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용실"));

        return quoteRequestRepository.findQuoteRequestsBySigunguId(shop.getSigunguId().getSigunguId());
    }

    // 미용사가 견적서 보낸 견적 요청 조회
    @Override
    public List<GetGroomerSendQuoteRequestResponseDto> getGroomerSendQuoteRequest(Long groomerId) {
        return quoteRequestRepository.findSendQuoteRequestsByGroomerId(groomerId);
    }

    // 미용사 견적서 요청 상세 조회
    @Override
    public GetGroomerRequestDetailResponseDto getGroomerRequestDetail(Long requestId) {
        // 견적서 요청
        QuoteRequest quoteRequest = quoteRequestRepository.findById(requestId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

        // dog
        Dog dog = dogRepository.findById(quoteRequest.getDogId().getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        // user
        Customer customer = customerRepository.findById(dog.getCustomerId().getCustomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        User user = userRepository.findById(customer.getUserId().getUserId())
                .orElseThrow(() -> NotFoundException.entityNotFound("사용자"));

        // quoteRequestImage
        List<QuoteRequestImage> getQuoteRequestImageList = quoteRequestImageRepository.findAllByRequestId(requestId);
        List<String> quoteRequestImageList = new ArrayList<>();
        for (QuoteRequestImage quoteRequestImage : getQuoteRequestImageList) {
            quoteRequestImageList.add(quoteRequestImage.getImageUrl());
        }

        return GetGroomerRequestDetailResponseDto.builder()
                .requestId(quoteRequest.getRequestId())
                .expiryDate(quoteRequest.getCreatedAt().plusDays(2))
                .beautyDate(quoteRequest.getBeautyDate())
                .requestContent(quoteRequest.getContent())
                .userProfileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .dogId(dog.getDogId())
                .dogProfileImage(dog.getProfileImage())
                .dogName(dog.getDogName())
                .dogBreed(dog.getDogBreed())
                .dogWeight(dog.getDogWeight())
                .dogAge(dog.getDogAge())
                .dogGender(dog.getDogGender().name())
                .neutering(dog.getNeutering())
                .experience(dog.getExperience())
                .significant(dog.getSignificant())
                .requestImages(quoteRequestImageList)
                .build();
    }

    // 미용사 1:1 맞춤 견적 요청 거절
    @Override
    @Transactional
    public UpdateGroomerRequestRejectionResponseDto updateGroomerRequestRejection(UpdateGroomerRequestRejectionRequestDto requestDto) {
        QuoteRequest quoteRequest = quoteRequestRepository.findById(requestDto.getRequestId())
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

        QuoteRequest updateQuoteRequest = QuoteRequest.builder()
                .requestId(requestDto.getRequestId())
                .dogId(quoteRequest.getDogId())
                .content(quoteRequest.getContent())
                .beautyDate(quoteRequest.getBeautyDate())
                .status("020")
                .requestType(quoteRequest.getRequestType())
                .build();

        QuoteRequest savedQuoteRequest = quoteRequestRepository.save(updateQuoteRequest);

        Groomer groomer = groomerRepository.findById(requestDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        DirectQuoteRequestId directQuoteRequestId = DirectQuoteRequestId.builder()
                .requestId(quoteRequest)
                .groomerId(groomer)
                .build();

        DirectQuoteRequest updateDirectQuoteRequest = DirectQuoteRequest.builder()
                .directQuoteRequestId(directQuoteRequestId)
                .reasonForRejection(requestDto.getRejectionReason())
                .build();

        DirectQuoteRequest savedDirectQuoteRequest = directQuoteRequestRepository.save(updateDirectQuoteRequest);

        return UpdateGroomerRequestRejectionResponseDto.builder()
                .requestId(savedQuoteRequest.getRequestId())
                .dogId(savedQuoteRequest.getDogId().getDogId())
                .beautyDate(savedQuoteRequest.getBeautyDate())
                .content(savedQuoteRequest.getContent())
                .status(savedQuoteRequest.getStatus())
                .requestType(savedQuoteRequest.getRequestType())
                .groomerId(savedDirectQuoteRequest.getDirectQuoteRequestId().getRequestId().getRequestId())
                .reasonForRejection(savedDirectQuoteRequest.getReasonForRejection())
                .build();
    }

}
