package com.beautymeongdang.domain.quote.service.impl;

import com.beautymeongdang.domain.quote.dto.GetCustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GetSelectedQuoteDetailResponseDto;
import com.beautymeongdang.domain.quote.dto.GetGroomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.entity.QuoteRequestImage;
import com.beautymeongdang.domain.quote.repository.QuoteRequestImageRepository;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SelectedQuoteServiceImpl implements SelectedQuoteService {

    private final SelectedQuoteRepository selectedQuoteRepository;
    private final QuoteRequestImageRepository quoteRequestImageRepository;

    @Override
    public List<GetCustomerSelectedQuoteResponseDto> getSelectedQuotesForCustomer(Long customerId) {
        return selectedQuoteRepository.findCustomerSelectedQuotes(customerId);
    }

    @Override
    public List<GetGroomerSelectedQuoteResponseDto> getSelectedQuotesForGroomer(Long groomerId) {
        return selectedQuoteRepository.findGroomerSelectedQuotes(groomerId);
    }

    @Override
    public GetSelectedQuoteDetailResponseDto getQuoteDetail(Long selectedQuoteId) {
        GetSelectedQuoteDetailResponseDto mainData = selectedQuoteRepository.findQuoteDetailById(selectedQuoteId);

        List<String> requestImages = quoteRequestImageRepository.findAllByRequestId(mainData.getQuoteId())
                .stream()
                .map(QuoteRequestImage::getImageUrl)
                .toList();

        mainData.setRequestImage(requestImages);

        return mainData;
    }
}