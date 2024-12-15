package com.beautymeongdang.domain.quote.service.impl;


import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.notification.enums.NotificationType;
import com.beautymeongdang.domain.notification.service.NotificationService;
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
import com.beautymeongdang.global.common.entity.CommonCode;
import com.beautymeongdang.global.common.entity.CommonCodeId;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.common.repository.CommonCodeRepository;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.region.entity.Sido;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SidoRepository;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.beautymeongdang.domain.quote.dto.GetGroomerQuoteRequestResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final CommonCodeRepository commonCodeRepository;
    private final NotificationService notificationService;

    private static final String REQUEST_STATUS_GROUP_CODE = "100";
    private static final String DOG_BREED_GROUP_CODE = "400";
    private static final String REQUEST_TYPE_GROUP_CODE = "900";
    private final SidoRepository sidoRepository;

    /**
     * 전체 견적서 요청하기
     */
    @Override
    @Transactional
    public CreateInsertRequestAllResponseDto createInsertRequestAll(Long customerId, CreateInsertRequestAllRequestDto requestDto, List<MultipartFile> images) {
        if (images != null && images.size() > 3) {
            throw BadRequestException.invalidRequest("이미지 개수는 3장");
        }

        // requestType 코드로 변환 ("전체요청" -> "010")
        CommonCode typeCode = commonCodeRepository.findAll().stream()
                .filter(code -> code.getId().getGroupId().equals(REQUEST_TYPE_GROUP_CODE))
                .filter(code -> code.getCommonName().equals(requestDto.getRequestType()))
                .findFirst()
                .orElseThrow(() -> NotFoundException.entityNotFound("요청 타입 코드"));

        if (!typeCode.getId().getCodeId().equals("010")) {
            throw BadRequestException.invalidRequest("전체요청 타입");
        }


        // status는 항상 "요청"(010)으로 설정
        CommonCode statusCode = commonCodeRepository.findAll().stream()
                .filter(code -> code.getId().getGroupId().equals(REQUEST_STATUS_GROUP_CODE))
                .filter(code -> code.getId().getCodeId().equals("010"))
                .findFirst()
                .orElseThrow(() -> NotFoundException.entityNotFound("상태 코드"));


        Dog dog = dogRepository.findById(requestDto.getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        Sigungu sigungu = sigunguRepository.findById(requestDto.getSigunguId())
                .orElseThrow(() -> NotFoundException.entityNotFound("지역정보"));

        QuoteRequest quoteRequest = QuoteRequest.builder()
                .dogId(dog)
                .content(requestDto.getRequestContent())
                .beautyDate(requestDto.getBeautyDate())
                .requestType(typeCode.getId().getCodeId())
                .status(statusCode.getId().getCodeId())
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
                .requestType(typeCode.getCommonName())
                .requestContent(savedRequest.getContent())
                .beautyDate(savedRequest.getBeautyDate())
                .status(statusCode.getCommonName())
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
    public CreateInsertRequestGroomerResponseDto createInsertRequestGroomer(Long customerId, CreateInsertRequestGroomerRequestDto requestDto, List<MultipartFile> images) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));
        Long customerUserId = customer.getUserId().getUserId();

        Groomer groomer = groomerRepository.findById(requestDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));
        Long groomerUserId = groomer.getUserId().getUserId();

        // 같은 사용자인 경우(동일한 userId) 견적서 요청 불가
        if (customerUserId.equals(groomerUserId)) {
            throw new BadRequestException("고객과 미용사가 동일인물입니다. 자신에게 견적서를 요청할 수 없습니다.");
        }

        if (images != null && images.size() > 3) {
            throw BadRequestException.invalidRequest("이미지 개수는 3장");
        }

        // requestType 코드로 변환 ("1:1요청" -> "020")
        CommonCode typeCode = commonCodeRepository.findAll().stream()
                .filter(code -> code.getId().getGroupId().equals(REQUEST_TYPE_GROUP_CODE))
                .filter(code -> code.getCommonName().equals(requestDto.getRequestType()))
                .findFirst()
                .orElseThrow(() -> NotFoundException.entityNotFound("요청 타입 코드"));

        if (!typeCode.getId().getCodeId().equals("020")) {
            throw BadRequestException.invalidRequest("1:1요청 타입");
        }

        // status는 항상 "요청"(010)으로 설정
        CommonCode statusCode = commonCodeRepository.findAll().stream()
                .filter(code -> code.getId().getGroupId().equals(REQUEST_STATUS_GROUP_CODE))
                .filter(code -> code.getId().getCodeId().equals("010"))
                .findFirst()
                .orElseThrow(() -> NotFoundException.entityNotFound("상태 코드"));

        Dog dog = dogRepository.findById(requestDto.getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        QuoteRequest quoteRequest = QuoteRequest.builder()
                .dogId(dog)
                .content(requestDto.getRequestContent())
                .beautyDate(requestDto.getBeautyDate())
                .requestType(typeCode.getId().getCodeId())
                .status(statusCode.getId().getCodeId())
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

        // 알림 저장 로직 추가
        String notificationMessage = String.format(
                "고객에게 견적서 요청이 도착하였습니다. 고객: %s, 강아지: %s, 미용 일자: %s",
                customer.getUserId().getUserName(),
                dog.getDogName(),
                requestDto.getBeautyDate()
        );

        // 알림 저장
        notificationService.saveNotification(
                groomer.getUserId().getUserId(),
                "groomer",
                NotificationType.QUOTE_REQUEST.getDescription(),
                notificationMessage
        );

        return CreateInsertRequestGroomerResponseDto.builder()
                .requestId(savedRequest.getRequestId())
                .dogId(savedRequest.getDogId().getDogId())
                .requestType(typeCode.getCommonName())
                .requestContent(savedRequest.getContent())
                .beautyDate(savedRequest.getBeautyDate())
                .status(statusCode.getCommonName())
                .groomerId(groomer.getGroomerId())
                .quoteRequestImage(savedImages.stream()
                        .map(QuoteRequestImage::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }


    /**
     * 고객의 반려견 리스트 조회
     */
    @Override
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
    public GetDogInfoResponseDto getDogInfo(Long dogId, Long customerId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        CommonCodeId breedCodeId = new CommonCodeId(dog.getDogBreed(), DOG_BREED_GROUP_CODE);
        CommonCode breedCode = commonCodeRepository.findById(breedCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견종 코드"));

        return GetDogInfoResponseDto.builder()
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
                .image(dog.getProfileImage())
                .dogBreed(breedCode.getCommonName())
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
    public GetRequestGroomerShopResponseDto getGroomerShopInfo(Long groomerId) {

        Shop shop = shopRepository.findByGroomerId(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("매장"));

        return GetRequestGroomerShopResponseDto.builder()
                .shopImage(shop.getImageUrl())
                .groomerName(shop.getGroomerId().getUserId().getNickname())
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

        CommonCodeId commonCodeId = new CommonCodeId(dog.getDogBreed(), "400");
        CommonCode commonCode = commonCodeRepository.findById(commonCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견종"));
        String dogBreed = commonCode.getCommonName();

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
                .userName(user.getUserName())
                .dogId(dog.getDogId())
                .dogProfileImage(dog.getProfileImage())
                .dogName(dog.getDogName())
                .dogBreed(dogBreed)
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

        CommonCodeId requestStatusCodeId = new CommonCodeId(savedQuoteRequest.getStatus(), "100");
        CommonCode requestStatusCode = commonCodeRepository.findById(requestStatusCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청 진행 상태 코드"));
        String requestStatus = requestStatusCode.getCommonName();

        CommonCodeId requestTypeCodeId = new CommonCodeId(savedQuoteRequest.getRequestType(), "900");
        CommonCode requestTypeCode = commonCodeRepository.findById(requestTypeCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound(" 진견적서 요청 타입 구분 코드"));
        String requestType = requestTypeCode.getCommonName();

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
                .status(requestStatus)
                .requestType(requestType)
                .groomerId(savedDirectQuoteRequest.getDirectQuoteRequestId().getGroomerId().getGroomerId())
                .reasonForRejection(savedDirectQuoteRequest.getReasonForRejection())
                .build();
    }

    // 고객(자신)이 보낸 견적 요청 상세 조회
    @Override
    public GetCustomerRequestDetailResponseDto getCustomerRequestDetail(Long requestId) {
        QuoteRequest quoteRequest = quoteRequestRepository.findById(requestId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

        List<QuoteRequestImage> quoteRequestImageList = quoteRequestImageRepository.findAllByRequestId(requestId);
        List<String> quoteRequestImages = new ArrayList<>();
        quoteRequestImageList.forEach(quoteRequestImage -> {
            quoteRequestImages.add(quoteRequestImage.getImageUrl());
        });

        Dog dog = dogRepository.findById(quoteRequest.getDogId().getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        GetCustomerRequestDetailResponseDto getCustomerRequestDetailResponseDto = new GetCustomerRequestDetailResponseDto();

        if ("010".equals(quoteRequest.getRequestType())) {
            TotalQuoteRequest totalQuoteRequest = totalQuoteRequestRepository.findByRequestId(quoteRequest);

            Sigungu sigungu = sigunguRepository.findById(totalQuoteRequest.getSigunguId().getSigunguId())
                    .orElseThrow(() -> NotFoundException.entityNotFound("시/군/구"));

            Sido sido = sidoRepository.findById(sigungu.getSidoId().getSidoId())
                    .orElseThrow(() -> NotFoundException.entityNotFound("시/도"));

            getCustomerRequestDetailResponseDto = GetCustomerRequestDetailResponseDto.builder()
                    .requestId(quoteRequest.getRequestId())
                    .requestType(quoteRequest.getRequestType())
                    .region(sido.getSidoName() + " " + sigungu.getSigunguName())
                    .beautyDate(quoteRequest.getBeautyDate())
                    .requestContent(quoteRequest.getContent())
                    .dogId(dog.getDogId())
                    .dogProfileImage(dog.getProfileImage())
                    .dogName(dog.getDogName())
                    .dogBreed(dog.getDogBreed())
                    .dogWeight(dog.getDogWeight())
                    .dogAge(dog.getDogAge())
                    .dogGender(String.valueOf(dog.getDogGender()))
                    .neutering(dog.getNeutering())
                    .experience(dog.getExperience())
                    .significant(dog.getSignificant())
                    .requestImages(quoteRequestImages)
                    .build();

        } else {
            Optional<DirectQuoteRequest> directQuoteRequest = directQuoteRequestRepository.findByQuoteRequest(quoteRequest);
            if (directQuoteRequest.isPresent()) {
                Optional<Shop> shop = shopRepository.findByGroomerId(directQuoteRequest.get().getDirectQuoteRequestId().getGroomerId().getGroomerId());

                User user = userRepository.findById(directQuoteRequest.get().getDirectQuoteRequestId().getGroomerId().getUserId().getUserId())
                        .orElseThrow(() -> NotFoundException.entityNotFound("미용사 정보"));

                GetCustomerRequestDetailResponseDto.GroomerInfo groomerInfo = GetCustomerRequestDetailResponseDto.GroomerInfo.builder()
                        .shopImage(shop.get().getImageUrl())
                        .groomerName(user.getNickname())
                        .shopName(shop.get().getShopName())
                        .address(shop.get().getAddress())
                        .phone(user.getPhone())
                        .build();

                getCustomerRequestDetailResponseDto = GetCustomerRequestDetailResponseDto.builder()
                        .requestId(quoteRequest.getRequestId())
                        .requestType(quoteRequest.getRequestType())
                        .groomer(groomerInfo)
                        .beautyDate(quoteRequest.getBeautyDate())
                        .requestContent(quoteRequest.getContent())
                        .dogId(dog.getDogId())
                        .dogProfileImage(dog.getProfileImage())
                        .dogName(dog.getDogName())
                        .dogBreed(dog.getDogBreed())
                        .dogWeight(dog.getDogWeight())
                        .dogAge(dog.getDogAge())
                        .dogGender(String.valueOf(dog.getDogGender()))
                        .neutering(dog.getNeutering())
                        .experience(dog.getExperience())
                        .significant(dog.getSignificant())
                        .requestImages(quoteRequestImages)
                        .build();

            }
        }

        return getCustomerRequestDetailResponseDto;
    }

}
