package com.beautymeongdang.domain.dog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetDogResponseDto {
    private Long customerId;
    private Long dogId;
    private String dogName;
    private String dogBreedCodeId;
    private String dogBreed;
    private String dogWeight;
    private String dogBirth;
    private String dogGender;
    private boolean neutering;
    private boolean experience;
    private String significant;
    private String dogProfileImage;
}