package com.beautymeongdang.domain.quote.service.impl;

import com.beautymeongdang.domain.quote.dto.CustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GroomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.entity.SelectedQuote;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelectedQuoteServiceImpl implements SelectedQuoteService {

    private final SelectedQuoteRepository selectedQuoteRepository;
    private final ShopRepository shopRepository;

    // 고객 예약 목록 조회
    @Override
    public List<CustomerSelectedQuoteResponseDto> getSelectedQuotesForCustomer(Long customerId) {
        List<SelectedQuote> selectedQuotes = selectedQuoteRepository.findAllByCustomerId(customerId);

        return selectedQuotes.stream()
                .map(this::toCustomerSelectedQuoteDto)
                .collect(Collectors.toList());
    }

    // 미용사 예약 목록 조회
    @Override
    public List<GroomerSelectedQuoteResponseDto> getSelectedQuotesForGroomer(Long groomerId) {
        List<SelectedQuote> selectedQuotes = selectedQuoteRepository.findAllByGroomerId(groomerId);

        return selectedQuotes.stream()
                .map(this::toGroomerSelectedQuoteDto)
                .collect(Collectors.toList());
    }

    private CustomerSelectedQuoteResponseDto toCustomerSelectedQuoteDto(SelectedQuote selectedQuote) {

        String shopName = shopRepository.findByGroomerId(selectedQuote.getQuoteId().getGroomerId().getGroomerId())
                .map(shop -> shop.getShopName())
                .orElse("Shop 정보 없음");

        return CustomerSelectedQuoteResponseDto.builder()
                .selectedQuoteId(selectedQuote.getSelectedQuoteId())
                .quoteId(selectedQuote.getQuoteId().getQuoteId())
                .profileImage(selectedQuote.getQuoteId().getDogId().getProfileImage())
                .shopName(shopName)
                .GroomerName(selectedQuote.getQuoteId().getGroomerId().getUserId().getUserName())
                .beautyDate(selectedQuote.getQuoteId().getBeautyDate())
                .dogName(selectedQuote.getQuoteId().getDogId().getDogName())
                .status(selectedQuote.getStatus())
                .build();
    }

    private GroomerSelectedQuoteResponseDto toGroomerSelectedQuoteDto(SelectedQuote selectedQuote) {
        return GroomerSelectedQuoteResponseDto.builder()
                .selectedQuoteId(selectedQuote.getSelectedQuoteId())
                .quoteId(selectedQuote.getQuoteId().getQuoteId())
                .profileImage(selectedQuote.getQuoteId().getDogId().getProfileImage())
                .customerName(selectedQuote.getCustomerId().getUserId().getUserName())
                .nickName(selectedQuote.getCustomerId().getUserId().getNickname())
                .phone(selectedQuote.getCustomerId().getUserId().getPhone())
                .dogName(selectedQuote.getQuoteId().getDogId().getDogName())
                .beautyDate(selectedQuote.getQuoteId().getBeautyDate())
                .status(selectedQuote.getStatus())
                .build();
    }
}
