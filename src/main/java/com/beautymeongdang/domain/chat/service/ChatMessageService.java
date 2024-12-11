package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.CreateChatMessageRequestDto;
import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;


public interface ChatMessageService {

    // 메시지 전송
    CreateChatMessageResponseDto sendMessage(CreateChatMessageRequestDto messageRequestDto);

}