package com.beautymeongdang.domain.dog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteDogResponseDto {
    private Long dogId;
}
