package com.beautymeongdang.domain.dog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDogResponseDto {
    private Long customerId;
    private Long dogId;
    private String dogName;
    private String dogBreed;
    private String dogWeight;
    private String dogBirth;
    private String dogGender;
    private boolean neutering;
    private boolean experience;
    private String significant;
    private String dogProfileImage;

}