package com.beautymeongdang.domain.quote.service.impl;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.notification.enums.NotificationType;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.beautymeongdang.domain.quote.dto.*;
import com.beautymeongdang.domain.quote.entity.*;
import com.beautymeongdang.domain.quote.repository.*;
import com.beautymeongdang.domain.quote.service.QuoteService;
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
import com.beautymeongdang.global.common.repository.CommonCodeRepository;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRequestRepository quoteRequestRepository;
    private final QuoteRepository quoteRepository;
    private final QuoteRequestImageRepository quoteRequestImageRepository;
    private final ShopRepository shopRepository;
    private final DirectQuoteRequestRepository directQuoteRequestRepository;
    private final GroomerRepository groomerRepository;
    private final DogRepository dogRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final NotificationService notificationService;
    private final TotalQuoteRequestRepository totalQuoteRequestRepository;

    private static final String QUOTE_REQUEST_STATUS_GROUP_CODE = "100";
    private static final String QUOTE_STATUS_GROUP_CODE = "200";
    private static final String DOG_BREED_GROUP_CODE = "400";


    /**
     * 고객이 자기가 보낸 견적(1:1) 요청을 조회
     */
    @Override
    public GetQuotesGroomerResponseDto getQuotesGroomer(Long customerId) {
        List<QuoteRequest> quoteRequests = quoteRequestRepository.findAllByCustomerId(customerId);

        List<GetQuotesGroomerResponseDto.QuoteRequestInfo> quoteRequestInfos = quoteRequests.stream()
                .map(request -> {
                    // DirectQuoteRequest에서 미용사 정보 조회
                    DirectQuoteRequest directRequest = directQuoteRequestRepository.findByQuoteRequest(request)
                            .orElseThrow(() -> NotFoundException.entityNotFound("직접 견적 요청"));

                    Groomer groomer = directRequest.getDirectQuoteRequestId().getGroomerId();
                    Shop shop = shopRepository.findByGroomerId(groomer.getGroomerId())
                            .orElseThrow(() -> NotFoundException.entityNotFound("미용실"));

                    CommonCodeId requestStatusCodeId = new CommonCodeId(request.getStatus(), QUOTE_REQUEST_STATUS_GROUP_CODE);
                    CommonCode requestStatusCode = commonCodeRepository.findById(requestStatusCodeId)
                            .orElseThrow(() -> NotFoundException.entityNotFound("견적 요청 상태 코드"));

                    // Quote 조회 - 거절이 아닐 때만
                    Long quoteId = null;
                    LocalDateTime expireDate = null;
                    if (!request.getStatus().equals("020")) {
                        Quote quote = quoteRepository.findByRequestIdAndGroomerIdAndIsDeletedFalse(request, groomer);
                        if (quote != null) {
                            quoteId = quote.getQuoteId();
                            expireDate = quote.getCreatedAt().plusDays(2);
                        }
                    }

                    return GetQuotesGroomerResponseDto.QuoteRequestInfo.builder()
                            .quoteRequestId(request.getRequestId())
                            .petName(request.getDogId().getDogName())
                            .petImage(request.getDogId().getProfileImage())
                            .status(requestStatusCode.getCommonName())
                            .shopId(shop.getShopId())
                            .shopName(shop.getShopName())
                            .groomerName(groomer.getUserId().getNickname())
                            .beautyDate(request.getBeautyDate())
                            .requestContent(request.getContent())
                            .quoteId(quoteId)
                            .rejectReason(directRequest.getReasonForRejection())
                            .expireDate(expireDate)
                            .build();
                })
                .collect(Collectors.toList());

        return GetQuotesGroomerResponseDto.builder()
                .quoteRequests(quoteRequestInfos)
                .build();
    }


    /**
     * 고객이 자기가 보낸 견적(전체) 요청을 조회
     */
    @Override
    public GetQuotesAllResponseDto getQuotesAll(Long customerId) {
        List<QuoteRequest> requests = quoteRequestRepository.findAllRequestsByCustomerId(customerId);

        List<GetQuotesAllResponseDto.QuoteRequestInfo> requestInfos = requests.stream()
                .map(request -> {
                    CommonCodeId requestStatusCodeId = new CommonCodeId(request.getStatus(), QUOTE_REQUEST_STATUS_GROUP_CODE);
                    CommonCode requestStatusCode = commonCodeRepository.findById(requestStatusCodeId)
                            .orElseThrow(() -> NotFoundException.entityNotFound("견적 요청 상태 코드"));
                    String requestStatusName = requestStatusCode.getCommonName();

                    List<Quote> quotes = quoteRepository.findAllByRequestId(request.getRequestId());
                    List<GetQuotesAllResponseDto.QuoteInfo> quoteInfos = quotes.stream()
                            .map(quote -> {
                                CommonCodeId quoteStatusCodeId = new CommonCodeId(quote.getStatus(), QUOTE_STATUS_GROUP_CODE);
                                CommonCode quoteStatusCode = commonCodeRepository.findById(quoteStatusCodeId)
                                        .orElseThrow(() -> NotFoundException.entityNotFound("견적서 상태 코드"));
                                String quoteStatusName = quoteStatusCode.getCommonName();

                                Shop shop = shopRepository.findByGroomerId(quote.getGroomerId().getGroomerId())
                                        .orElseThrow(() -> NotFoundException.entityNotFound("미용실"));

                                LocalDateTime expireDate = quote.getCreatedAt().plusDays(2);


                                return GetQuotesAllResponseDto.QuoteInfo.builder()
                                        .quoteId(quote.getQuoteId())
                                        .shopName(shop.getShopName())
                                        .shopLogo(shop.getImageUrl())
                                        .groomerName(quote.getGroomerId().getUserId().getNickname())
                                        .quoteStatus(quoteStatusName)
                                        .cost(quote.getCost())
                                        .quoteContent(quote.getContent())
                                        .createdAt(quote.getCreatedAt())
                                        .expireDate(expireDate)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    TotalQuoteRequest totalQuoteRequest = totalQuoteRequestRepository.findByRequestId_RequestId(request.getRequestId())
                            .orElseThrow(() -> new NotFoundException("견적서 전체 요청"));

                    String region = totalQuoteRequest.getSigunguId().getSidoId().getSidoName() + " " + totalQuoteRequest.getSigunguId().getSigunguName();

                    return GetQuotesAllResponseDto.QuoteRequestInfo.builder()
                            .quoteRequestId(request.getRequestId())
                            .requestStatus(requestStatusName)
                            .beautyDate(request.getBeautyDate())
                            .dogName(request.getDogId().getDogName())
                            .dogImage(request.getDogId().getProfileImage())
                            .requestContent(request.getContent())
                            .region(region)
                            .quotes(quoteInfos)
                            .build();
                })
                .collect(Collectors.toList());

        return GetQuotesAllResponseDto.builder()
                .quoteRequests(requestInfos)
                .build();
    }


    /**
     고객이 받은 견적서 상세 조회 (요청+견적서)
     */
    @Override
    public GetQuoteDetailResponseDto getQuoteDetail(Long quoteId, Long customerId) {
        Quote quote = quoteRepository.findQuoteDetailById(quoteId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서"));

        Shop shop = shopRepository.findByGroomerId(quote.getGroomerId().getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("샵 정보"));

        List<QuoteRequestImage> requestImages = quoteRequestImageRepository
                .findAllByRequestId(quote.getRequestId().getRequestId());

        // db에 있는 견종 코드를 견종명으로 변환
        CommonCodeId breedCodeId = new CommonCodeId(quote.getDogId().getDogBreed(), DOG_BREED_GROUP_CODE);
        CommonCode breedCode = commonCodeRepository.findById(breedCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견종 코드"));
        String dogBreedName = breedCode.getCommonName();


        return GetQuoteDetailResponseDto.builder()
                .groomer(GetQuoteDetailResponseDto.GroomerInfo.builder()
                        .shopLogo(shop.getImageUrl())
                        .groomerName(quote.getGroomerId().getUserId().getNickname())
                        .shopName(shop.getShopName())
                        .address(shop.getAddress())
                        .phone(quote.getGroomerId().getUserId().getPhone())
                        .build())
                .quoteRequest(GetQuoteDetailResponseDto.QuoteRequestInfo.builder()
                        .dogName(quote.getDogId().getDogName())
                        .dogImage(quote.getDogId().getProfileImage())
                        .dogWeight(quote.getDogId().getDogWeight())
                        .dogAge(String.valueOf(quote.getDogId().getDogAge()))
                        .dogGender(quote.getDogId().getDogGender().toString())
                        .dogBreed(dogBreedName)
                        .neutering(quote.getDogId().getNeutering())
                        .experience(quote.getDogId().getExperience())
                        .significant(quote.getDogId().getSignificant())
                        .requestContent(quote.getRequestId().getContent())
                        .requestImage(requestImages.stream()
                                .map(QuoteRequestImage::getImageUrl)
                                .collect(Collectors.toList()))
                        .build())
                .quote(GetQuoteDetailResponseDto.QuoteInfo.builder()
                        .quoteId(quote.getQuoteId())
                        .beautyDate(quote.getBeautyDate())
                        .cost(quote.getCost())
                        .quoteContent(quote.getContent())
                        .build())
                .build();
    }


    // 미용사 견적서 작성
    @Override
    @Transactional
    public CreateGroomerQuoteResponseDto createGroomerQuote(CreateGroomerQuoteRequestDto requestDto) {

        QuoteRequest quoteRequest = quoteRequestRepository.findById(requestDto.getRequestId())
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

        Groomer groomer = groomerRepository.findById(requestDto.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Dog dog = dogRepository.findById(requestDto.getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        Quote quote = Quote.builder()
                .requestId(quoteRequest)
                .groomerId(groomer)
                .dogId(dog)
                .content(requestDto.getQuoteContent())
                .cost(requestDto.getQuoteCost())
                .beautyDate(requestDto.getBeautyDate())
                .status("010")
                .build();

        Quote saveQuote = quoteRepository.save(quote);

        if(quoteRequest.getRequestType().equals("020")) {
            QuoteRequest updateQuoteRequest = QuoteRequest.builder()
                    .requestId(quoteRequest.getRequestId())
                    .dogId(quoteRequest.getDogId())
                    .content(quoteRequest.getContent())
                    .beautyDate(quoteRequest.getBeautyDate())
                    .requestType(quoteRequest.getRequestType())
                    .status("040")
                    .build();

            quoteRequestRepository.save(updateQuoteRequest);
        }
        // 견적서 알림 메시지 생성
        String notificationMessage = String.format(
                "견적서가 도착하였습니다. 미용사: %s, 강아지: %s, 비용: %d원",
                groomer.getUserId().getNickname(),
                dog.getDogName(),
                requestDto.getQuoteCost()
        );

        // 알림 저장
        notificationService.saveNotification(
                quoteRequest.getDogId().getCustomerId().getUserId().getUserId(),
                "customer",
                NotificationType.QUOTE.getDescription(),
                notificationMessage
        );


        CommonCodeId commonCodeId = new CommonCodeId(saveQuote.getStatus(), "100");
        CommonCode commonCode = commonCodeRepository.findById(commonCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("요청 진행 상태"));
        String requestStatus = commonCode.getCommonName();

        return CreateGroomerQuoteResponseDto.builder()
                .quoteId(saveQuote.getQuoteId())
                .requestId(saveQuote.getRequestId().getRequestId())
                .groomerId(saveQuote.getGroomerId().getGroomerId())
                .dogId(saveQuote.getDogId().getDogId())
                .quoteContent(saveQuote.getContent())
                .quoteCost(saveQuote.getCost())
                .beautyDate(saveQuote.getBeautyDate())
                .quoteStatus(requestStatus)
                .build();
    }

    // 미용사가 보낸 견적서 상세 조회
    @Override
    public GetGroomerQuoteDetailResponseDto getGroomerQuoteDetail(Long requestId, Long groomerId) {
        QuoteRequest quoteRequest = quoteRequestRepository.findById(requestId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견적서 요청"));

        Dog dog = dogRepository.findById(quoteRequest.getDogId().getDogId())
                .orElseThrow(() -> NotFoundException.entityNotFound("강아지"));

        CommonCodeId commonCodeId = new CommonCodeId(dog.getDogBreed(), "400");
        CommonCode commonCode = commonCodeRepository.findById(commonCodeId)
                .orElseThrow(() -> NotFoundException.entityNotFound("견종"));
        String dogBreed = commonCode.getCommonName();

        Customer customer = customerRepository.findById(dog.getCustomerId().getCustomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        User user = userRepository.findById(customer.getUserId().getUserId())
                .orElseThrow(() -> NotFoundException.entityNotFound("사용자"));

        Groomer groomer = groomerRepository.findById(groomerId)
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        Quote quote = quoteRepository.findByRequestIdAndGroomerId(quoteRequest, groomer);
        if (quote == null) {
            throw NotFoundException.entityNotFound("해당 견적서를 찾을 수 없습니다.");
        }

        List<QuoteRequestImage> getQuoteRequestImageList = quoteRequestImageRepository.findAllByRequestId(requestId);
        List<String> quoteRequestImageList = new ArrayList<>();
        for (QuoteRequestImage quoteRequestImage : getQuoteRequestImageList) {
            quoteRequestImageList.add(quoteRequestImage.getImageUrl());
        }


        return GetGroomerQuoteDetailResponseDto.builder()
                .customer(GetGroomerQuoteDetailResponseDto.CustomerInfo.builder()
                        .profileImage(user.getProfileImage())
                        .userName(user.getUserName())
                        .build())
                .dog(GetGroomerQuoteDetailResponseDto.DogInfo.builder()
                        .dogProfileImage(dog.getProfileImage())
                        .dogName(dog.getDogName())
                        .dogBreed(dogBreed)
                        .dogWeight(dog.getDogWeight())
                        .dogAge(dog.getDogAge())
                        .dogGender(dog.getDogGender().name())
                        .neutering(dog.getNeutering())
                        .experience(dog.getExperience())
                        .significant(dog.getSignificant())
                        .build())
                .quote(GetGroomerQuoteDetailResponseDto.QuoteInfo.builder()
                        .requestContent(quoteRequest.getContent())
                        .beautyDate(quote.getBeautyDate())
                        .quoteCost(quote.getCost())
                        .quoteContent(quote.getContent())
                        .requestImageUrl(quoteRequestImageList)
                        .build())
                .build();
    }

}