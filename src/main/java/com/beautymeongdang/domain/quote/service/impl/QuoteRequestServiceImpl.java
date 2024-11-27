package com.beautymeongdang.domain.quote.service.impl;

import com.beautymeongdang.domain.quote.dto.GroomerDirectRequestListResponseDto;
import com.beautymeongdang.domain.quote.repository.QuoteRequestRepository;
import com.beautymeongdang.domain.quote.service.QuoteRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuoteRequestServiceImpl implements QuoteRequestService {

    private final QuoteRequestRepository quoteRequestRepository;

    // 미용사가 받은 1:1 요청 조회
    @Override
    public List<GroomerDirectRequestListResponseDto> getGroomerDirectRequestList(Long groomerId) {
        return quoteRequestRepository.findQuoteRequestsByGroomerId(groomerId);
    }

}
