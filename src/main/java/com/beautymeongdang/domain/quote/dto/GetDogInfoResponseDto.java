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
    private String name;
    private String image;
    private String weight;
    private Integer age;
    private String dogGender;
    private Boolean neutering;
    private Boolean experience;
    private String significant;
}