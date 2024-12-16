package com.beautymeongdang.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetMyGroomerShopDetailResponseDto {
    private Long groomerId;
    private Long shopId;
    private String shopLogo;
    private String shopName;
    private Double starScoreAvg;
    private Integer starCount;
    private String address;
    private String businessTime;
    private String skills;
    private Double latitude;
    private Double longitude;
    private Integer favoriteCount;
    private String description;
    private List<String> groomerPortfolioImages;
    private String groomerUsername;
    private String groomerProfileImage;
    private List<ReviewDetailDto> reviews;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReviewDetailDto {
        private Long reviewId;
        private String customerProfile;
        private String customerNickname;
        private Double starScore;
        private String content;
        private Integer recommendCount;
        private List<String> reviewsImage;
        private LocalDateTime createdAt;
    }
}