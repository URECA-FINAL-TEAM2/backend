package com.beautymeongdang.global.region.service;

import com.beautymeongdang.global.region.dto.SigunguDto;

import java.util.List;

public interface SigunguService {
    List<SigunguDto> getSigunguList(Long sidoId);
}