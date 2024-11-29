package com.beautymeongdang.global.region.service.impl;

import com.beautymeongdang.global.region.dto.GetSidoResponseDto;
import com.beautymeongdang.global.region.dto.GetSigunguResponseDto;
import com.beautymeongdang.global.region.entity.Sido;
import com.beautymeongdang.global.region.entity.Sigungu;
import com.beautymeongdang.global.region.repository.SidoRepository;
import com.beautymeongdang.global.region.repository.SigunguRepository;
import com.beautymeongdang.global.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final SidoRepository sidoRepository;
    private final SigunguRepository sigunguRepository;

    /**
     * 시도 조회
     */
    @Override
    @Transactional(readOnly = true)
    public GetSidoResponseDto getSidoList() {
        List<Sido> sidoList = sidoRepository.findAll();
        
        List<GetSidoResponseDto.SidoDto> sidoDtoList = sidoList.stream()
            .map(sido -> GetSidoResponseDto.SidoDto.builder()
                .sidoId(sido.getSidoId())
                .sidoName(sido.getSidoName())
                .build())
            .collect(Collectors.toList());

        return GetSidoResponseDto.builder()
            .sidoList(sidoDtoList)
            .build();
    }


    /**
     * 시군구 조회
     */
    @Override
    @Transactional(readOnly = true)
    public GetSigunguResponseDto getSigunguList(Long sidoId) {
        List<Sigungu> sigunguList = sigunguRepository.findBySidoId_SidoId(sidoId);

        List<GetSigunguResponseDto.SigunguDto> sigunguDtoList = sigunguList.stream()
                .map(sigungu -> GetSigunguResponseDto.SigunguDto.builder()
                        .sigunguId(sigungu.getSigunguId())
                        .sigunguName(sigungu.getSigunguName())
                        .build())
                .collect(Collectors.toList());

        return GetSigunguResponseDto.builder()
                .sigunguList(sigunguDtoList)
                .build();
    }


}