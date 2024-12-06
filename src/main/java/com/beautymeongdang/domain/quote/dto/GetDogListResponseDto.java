package com.beautymeongdang.domain.quote.dto;

import com.beautymeongdang.domain.dog.entity.Dog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetDogListResponseDto {
    private Long dogId;
    private String dogName;
    private String profileImage;

    public static GetDogListResponseDto of(Dog dog) {
        return GetDogListResponseDto.builder()
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
                .profileImage(dog.getProfileImage())
                .build();
    }
}