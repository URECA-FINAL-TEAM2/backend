package com.beautymeongdang.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetChatMessageListResponseDto {
    private GroomerInfo groomerInfo;
    private CustomerInfo customerInfo;
    private List<GetChatMessageResponseDto> messages;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  GroomerInfo{
        private String groomerProfileImage;
        private String groomerName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class  CustomerInfo{
        private String customerProfileImage;
        private String customerName;
    }

}
