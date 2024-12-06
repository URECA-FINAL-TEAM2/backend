package com.beautymeongdang.domain.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetDogInfoResponseDto {
    private Long dogId;
    private String dogName;
    private String image;
    private String dogBreed;
    private String dogWeight;
    private Integer dogAge;
    private String dogGender;
    private Boolean neutering;
    private Boolean experience;
    private String significant;
}