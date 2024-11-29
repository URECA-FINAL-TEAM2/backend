package com.beautymeongdang.global.region.service.impl;

import com.beautymeongdang.global.region.dto.GetSidoResponseDto;
import com.beautymeongdang.global.region.entity.Sido;
import com.beautymeongdang.global.region.repository.SidoRepository;
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




}