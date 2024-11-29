package com.beautymeongdang.global.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSigunguResponseDto {
    private List<SigunguDto> sigunguList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SigunguDto {
        private Long sigunguId;
        private String sigunguName;
    }
}