package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.*;


public interface ChatMessageService {

    // 메시지 전송
    CreateChatMessageResponseDto sendMessage(CreateChatMessageRequestDto messageRequestDto);

    // 채팅 조회
    GetChatMessageListResponseDto getChatMessageList(Long chatId);

    // 채팅 논리적 삭제
    DeleteChatMessageResponseDto deleteChatMessage(Long messageId);

    // 일반적인 메시지 읽음 처리
    void markMessagesAsRead(Long chatId, Long userId);

    // 채팅방 입장 시 기존 메시지 읽음 처리
    void processUserEntrance(Long chatId, Long userId, Boolean isCustomer);

    // 안 읽은 메시지 수
    GetUnreadMessageCountResponseDto getUnreadMessageCount(Long chatId, Long userId);


}