package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.*;

import java.util.List;

public interface ChatService {
    CreateChatResponseDto createChat(CreateChatRequestDto request);

    void validateChatRoomAccess(Long chatId, Long userId, Boolean customerYn);

    // 고객 채팅방 목록 조회
    List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId);

    // 고객 채팅방 목록 검색 조회
    List<GetCustomerChatListResponseDto> getCustomerChatListBySearchKeyword(Long customerId, String searchWord);

    // 미용사 채팅방 목록 조회
    List<GetGroomerChatListResponseDto> getGroomerChatList(Long customerId);

    // 미용사 채팅방 목록 검색 조회
    List<GetGroomerChatListResponseDto> getGroomerChatListBySearchKeyword(Long groomerId, String searchWord);

}
