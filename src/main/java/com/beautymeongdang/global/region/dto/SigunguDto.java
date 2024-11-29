package com.beautymeongdang.global.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigunguDto {
    private Long sigunguId;
    private String sigunguName;
    private Long sidoId;
}

