package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.*;

import java.util.List;

public interface ChatService {
    // 채팅방 생성
    CreateChatResponseDto createChat(CreateChatRequestDto request);

    // 채팅방 조회 및 접근 권한 검증을 동시에 수행
    void validateChatRoomAccess(Long chatId, Long userId, Boolean customerYn);

    // 채팅방 나가기
    UpdateChatExitResponseDto exitChat(UpdateChatExitRequestDto requestDto);

    // 고객 채팅방 목록 조회
    List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId);

    // 고객 채팅방 목록 검색 조회
    List<GetCustomerChatListResponseDto> getCustomerChatListBySearchKeyword(GetCustomerKeywordChatListRequestDto requestDto);

    // 미용사 채팅방 목록 조회
    List<GetGroomerChatListResponseDto> getGroomerChatList(Long customerId);

    // 미용사 채팅방 목록 검색 조회
    List<GetGroomerChatListResponseDto> getGroomerChatListBySearchKeyword(GetGroomerKeywordChatListRequestDto requestDto);

}
