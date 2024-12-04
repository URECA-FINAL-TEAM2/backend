package com.beautymeongdang.domain.dog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateDogRequestDto {
    private String dogName;
    private String dogBreed;
    private String dogWeight;
    private String dogBirth;
    private String dogGender;
    private boolean neutering;
    private boolean experience;
    private String significant;
}