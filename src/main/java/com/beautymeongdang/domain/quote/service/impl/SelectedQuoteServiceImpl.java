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
                .map(this::mapToCustomerResponseDto)
                .collect(Collectors.toList());
    }

    // 미용사 예약 목록 조회
    @Override
    public List<GroomerSelectedQuoteResponseDto> getSelectedQuotesForGroomer(Long groomerId) {
        List<SelectedQuote> selectedQuotes = selectedQuoteRepository.findAllByGroomerId(groomerId);

        return selectedQuotes.stream()
                .map(this::mapToGroomerResponseDto)
                .collect(Collectors.toList());
    }

    // CustomerSelectedQuoteResponseDto 매핑
    private CustomerSelectedQuoteResponseDto mapToCustomerResponseDto(SelectedQuote selectedQuote) {
        // Shop 데이터를 ShopRepository에서 가져옴
        String shopName = shopRepository.findByGroomerId(selectedQuote.getQuoteId().getGroomerId().getGroomerId())
                .map(shop -> shop.getShopName())
                .orElse("Shop 정보 없음");

        return CustomerSelectedQuoteResponseDto.builder()
                .selectedQuoteId(selectedQuote.getSelectedQuoteId())
                .quoteId(selectedQuote.getQuoteId().getQuoteId())
                .profileImage(selectedQuote.getQuoteId().getGroomerId().getUserId().getProfileImage())
                .shopName(shopName)
                .GroomerName(selectedQuote.getQuoteId().getGroomerId().getUserId().getNickname())
                .nickname(selectedQuote.getCustomerId().getUserId().getNickname())
                .beautyDate(selectedQuote.getQuoteId().getBeautyDate())
                .dogName(selectedQuote.getQuoteId().getDogId().getDogName())
                .status(selectedQuote.getStatus())
                .build();
    }

    // GroomerSelectedQuoteResponseDto 매핑
    private GroomerSelectedQuoteResponseDto mapToGroomerResponseDto(SelectedQuote selectedQuote) {
        return GroomerSelectedQuoteResponseDto.builder()
                .selectedQuoteId(selectedQuote.getSelectedQuoteId())
                .quoteId(selectedQuote.getQuoteId().getQuoteId())
                .profileImage(selectedQuote.getCustomerId().getUserId().getProfileImage())
                .customerName(selectedQuote.getCustomerId().getUserId().getNickname())
                .phone(selectedQuote.getCustomerId().getUserId().getPhone())
                .dogName(selectedQuote.getQuoteId().getDogId().getDogName())
                .beautyDate(selectedQuote.getQuoteId().getBeautyDate())
                .status(selectedQuote.getStatus())
                .build();
    }
}
