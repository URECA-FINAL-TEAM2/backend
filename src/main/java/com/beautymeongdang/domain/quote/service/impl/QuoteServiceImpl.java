package com.beautymeongdang.domain.quote.service.impl;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import com.beautymeongdang.domain.quote.dto.*;
import com.beautymeongdang.domain.quote.entity.DirectQuoteRequest;
import com.beautymeongdang.domain.quote.entity.Quote;
import com.beautymeongdang.domain.quote.entity.QuoteRequest;
import com.beautymeongdang.domain.quote.entity.QuoteRequestImage;
import com.beautymeongdang.domain.quote.repository.DirectQuoteRequestRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestImageRepository;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.service.QuoteService;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

                    return GetQuotesGroomerResponseDto.QuoteRequestInfo.builder()
                            .quoteRequestId(request.getRequestId())
                            .petName(request.getDogId().getDogName())
                            .petImage(request.getDogId().getProfileImage())
                            .status(request.getStatus())
                            .shopName(shop.getShopName())
                            .groomerName(groomer.getUserId().getUserName())
                            .beautyDate(request.getBeautyDate())
                            .requestContent(request.getContent())
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
                    List<Quote> quotes = quoteRepository.findAllByRequestId(request.getRequestId());
                    List<GetQuotesAllResponseDto.QuoteInfo> quoteInfos = quotes.stream()
                            .map(quote -> GetQuotesAllResponseDto.QuoteInfo.builder()
                                    .quoteId(quote.getQuoteId())
                                    .status(quote.getStatus())
                                    .cost(quote.getCost())
                                    .quoteContent(quote.getContent())
                                    .createdAt(quote.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    return GetQuotesAllResponseDto.QuoteRequestInfo.builder()
                            .quoteRequestId(request.getRequestId())
                            .status(request.getStatus())
                            .beautyDate(request.getBeautyDate())
                            .dogName(request.getDogId().getDogName())
                            .image(request.getDogId().getProfileImage())
                            .dogWeight(request.getDogId().getDogWeight())
                            .dogBreed(request.getDogId().getDogBreed())
                            .dogAge(String.valueOf(request.getDogId().getDogAge()))
                            .requestContent(request.getContent())
                            .quotes(quoteInfos)
                            .build();
                })
                .collect(Collectors.toList());

        return GetQuotesAllResponseDto.builder()
                .quoteRequests(requestInfos)
                .build();
    }

    /**
     견적서 상세 조회
     */
    @Override
    public GetQuoteDetailResponseDto getQuoteDetail(GetQuoteDetailRequestDto requestDto) {
        Quote quote = quoteRepository.findQuoteDetailById(requestDto.getQuoteId())
                .orElseThrow(() -> new EntityNotFoundException("견적서를 찾을 수 없습니다."));

        // Shop 정보 조회
        Shop shop = shopRepository.findByGroomerId(quote.getGroomerId().getGroomerId())
                .orElseThrow(() -> new EntityNotFoundException("샵 정보를 찾을 수 없습니다."));

        List<QuoteRequestImage> requestImages = quoteRequestImageRepository
                .findAllByRequestId(quote.getRequestId().getRequestId());

        return GetQuoteDetailResponseDto.builder()
                .groomer(GetQuoteDetailResponseDto.GroomerInfo.builder()
                        .groomerName(quote.getGroomerId().getUserId().getUserName())
                        .shopName(shop.getShopName())
                        .address(shop.getAddress())
                        .phone(quote.getGroomerId().getUserId().getPhone())
                        .build())
                .quoteRequest(GetQuoteDetailResponseDto.QuoteRequestInfo.builder()
                        .name(quote.getDogId().getDogName())
                        .image(quote.getDogId().getProfileImage())
                        .weight(quote.getDogId().getDogWeight())
                        .age(String.valueOf(quote.getDogId().getDogAge()))
                        .dogGender(quote.getDogId().getDogGender().toString())
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

        return CreateGroomerQuoteResponseDto.builder()
                .quoteId(saveQuote.getQuoteId())
                .requestId(saveQuote.getRequestId().getRequestId())
                .groomerId(saveQuote.getGroomerId().getGroomerId())
                .dogId(saveQuote.getDogId().getDogId())
                .quoteContent(saveQuote.getContent())
                .quoteCost(saveQuote.getCost())
                .beautyDate(saveQuote.getBeautyDate())
                .quoteStatus(saveQuote.getStatus())
                .build();
    }

}