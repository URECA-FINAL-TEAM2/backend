package com.beautymeongdang.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class UpdateGroomerPortfolioDto {
    private Long groomerId;
    private List<String> images;

    @Builder
    public UpdateGroomerPortfolioDto(Long groomerId, List<String> images) {
        this.groomerId = groomerId;
        this.images = images;
    }
}
