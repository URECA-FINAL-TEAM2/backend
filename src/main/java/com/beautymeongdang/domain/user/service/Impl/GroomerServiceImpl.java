package com.beautymeongdang.domain.user.service.Impl;

import com.beautymeongdang.domain.user.dto.GetGroomerProfileResponseDto;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.domain.user.service.GroomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroomerServiceImpl implements GroomerService {
    private final GroomerRepository groomerRepository;

    // 미용사 정보 조회
    @Override
    public GetGroomerProfileResponseDto getGroomerProfile(Long groomerId) {
        return groomerRepository.findGroomerInfoById(groomerId);
    }
}
