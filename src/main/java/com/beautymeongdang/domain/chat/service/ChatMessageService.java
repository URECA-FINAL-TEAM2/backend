package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.CreateChatMessageRequestDto;
import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;
import com.beautymeongdang.domain.chat.dto.DeleteChatMessageResponseDto;
import com.beautymeongdang.domain.chat.dto.GetChatMessageListResponseDto;


public interface ChatMessageService {

    // 메시지 전송
    CreateChatMessageResponseDto sendMessage(CreateChatMessageRequestDto messageRequestDto);

    // 채팅 조회
    GetChatMessageListResponseDto getChatMessageList(Long chatId);

    // 채팅 논리적 삭제
    DeleteChatMessageResponseDto deleteChatMessage(Long messageId);

}