package com.beautymeongdang.domain.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

public class GetShopDetailResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ShopDetailResponseDto {
        private String description;
        private List<String> groomerPortfolioImages;
        private String groomerUsername;
        private String groomerProfileImage;
        private List<ReviewDetailDto> reviews;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReviewDetailDto {
        private Long reviewId;
        private String customerNickname;
        private Double starScore;
        private String content;
        private Integer recommendCount;
        private List<String> reviewsImage;
        private LocalDateTime createdAt;
        @JsonProperty("isRecommended")
        private boolean recommended;
    }
}