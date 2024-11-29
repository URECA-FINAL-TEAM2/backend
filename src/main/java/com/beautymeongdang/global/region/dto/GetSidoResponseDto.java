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
public class GetSidoResponseDto {
    private List<SidoDto> sidoList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SidoDto {
        private Long sidoId;
        private String sidoName;
    }
}