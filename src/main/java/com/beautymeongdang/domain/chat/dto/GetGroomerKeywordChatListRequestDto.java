package com.beautymeongdang.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GetGroomerKeywordChatListRequestDto {
    private Long groomerId;
    private String searchWord;
}
