package com.beautymeongdang.global.region.service.Impl;

import com.beautymeongdang.global.region.dto.SigunguDto;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.global.region.service.SigunguService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SigunguServiceImpl implements SigunguService {
    private final SigunguRepository sigunguRepository;

    @Transactional(readOnly = true)
    @Override
    public List<SigunguDto> getSigunguList(Long sidoId) {
        List<Sigungu> sigunguList = sigunguRepository.findBySidoId_SidoId(sidoId);
        return sigunguList.stream()
                .map(sigungu -> SigunguDto.builder()
                        .sigunguId(sigungu.getSigunguId())
                        .sigunguName(sigungu.getSigunguName())
                        .sidoId(sigungu.getSidoId().getSidoId())
                        .build())
                .collect(Collectors.toList());
    }
}