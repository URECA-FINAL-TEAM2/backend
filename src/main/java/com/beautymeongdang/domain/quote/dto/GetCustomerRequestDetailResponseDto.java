package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerRequestDetailResponseDto {
    private Long requestId;
    private String requestType;
    private String region;
    private GroomerInfo groomer;
    private LocalDateTime beautyDate;
    private String requestContent;
    private Long dogId;
    private String dogProfileImage;
    private String dogName;
    private String dogBreed;
    private String dogWeight;
    private Integer dogAge;
    private String dogGender;
    private Boolean neutering;
    private Boolean experience;
    private String significant;
    private List<String> requestImages;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  GroomerInfo{
        private String shopImage;
        private String groomerName;
        private String shopName;
        private String address;
        private String phone;
    }

}
