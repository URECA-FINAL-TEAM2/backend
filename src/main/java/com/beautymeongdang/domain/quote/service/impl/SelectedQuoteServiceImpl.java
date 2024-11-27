package com.beautymeongdang.domain.quote.service.impl;

import com.beautymeongdang.domain.quote.dto.CustomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.dto.GroomerSelectedQuoteResponseDto;
import com.beautymeongdang.domain.quote.repository.SelectedQuoteRepository;
import com.beautymeongdang.domain.quote.service.SelectedQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SelectedQuoteServiceImpl implements SelectedQuoteService {

    private final SelectedQuoteRepository selectedQuoteRepository;

    @Override
    public List<CustomerSelectedQuoteResponseDto> getSelectedQuotesForCustomer(Long customerId) {
        return selectedQuoteRepository.findCustomerSelectedQuotes(customerId);
    }

    @Override
    public List<GroomerSelectedQuoteResponseDto> getSelectedQuotesForGroomer(Long groomerId) {
        return selectedQuoteRepository.findGroomerSelectedQuotes(groomerId);
    }
}