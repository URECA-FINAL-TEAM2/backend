package com.beautymeongdang.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

public class GetMainCustomerResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MainResponse {
        private List<BestReviewDto> bestReviews;
        private List<LocalGroomerDto> localGroomers;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BestReviewDto {
        private Long reviewId;
        private String shopName;
        private String reviewImage;
        private String content;
        private Double starScore;
        private Integer recommendCount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class LocalGroomerDto {
        private Long groomerId;
        private Long shopId;
        private String shopLogo;
        private String shopName;
        private Double starScoreAvg;
        private Integer reviewCount;
        private String address;
        private String businessTime;
        private String skills;
    }
}