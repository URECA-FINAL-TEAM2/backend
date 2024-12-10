package com.beautymeongdang.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRequestDto {
    private Long customerId;
    private Long groomerId;
}