package com.beautymeongdang.domain.dog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDogRequestDto {
    private Long customerId;
    private String dogName;
    private String breed;
    private String dogWeight;
    private String dogBirth;
    private String dogGender;
    private boolean neutering;
    private boolean experience;
    private String significant;
}