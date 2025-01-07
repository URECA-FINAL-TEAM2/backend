package com.beautymeongdang.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 메시지 읽음 처리를 요청할 때 사용하는 DTO
 * 사용자가 메시지를 읽었을 때 서버에 알리는 용도
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageReadRequestDto {
    private Long chatId;
    private Long senderId;
    private Boolean customerYn;
}