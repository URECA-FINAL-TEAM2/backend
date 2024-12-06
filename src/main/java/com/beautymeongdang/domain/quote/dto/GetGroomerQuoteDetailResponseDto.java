package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetGroomerQuoteDetailResponseDto {
    private CustomerInfo customer;
    private DogInfo dog;
    private QuoteInfo quote;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  CustomerInfo{
        private String profileImage;
        private String userName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  DogInfo{
        private String dogProfileImage;
        private String dogName;
        private String dogBreed;
        private String dogWeight;
        private Integer dogAge;
        private String dogGender;
        private Boolean neutering;
        private Boolean experience;
        private String significant;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  QuoteInfo{
        private String requestContent;
        private LocalDateTime beautyDate;
        private Integer quoteCost;
        private String quoteContent;
        private List<String> requestImageUrl;
    }

}
