package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.GetCustomerChatListResponseDto;
import com.beautymeongdang.domain.chat.dto.GetCustomerKeywordChatListRequestDto;
import com.beautymeongdang.domain.chat.dto.GetGroomerChatListResponseDto;
import com.beautymeongdang.domain.chat.dto.GetGroomerKeywordChatListRequestDto;

import java.util.List;

public interface ChatService {
    // 고객 채팅방 목록 조회
    List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId);

    // 고객 채팅방 목록 검색 조회
    List<GetCustomerChatListResponseDto> getCustomerChatListBySearchKeyword(GetCustomerKeywordChatListRequestDto requestDto);

    // 미용사 채팅방 목록 조회
    List<GetGroomerChatListResponseDto> getGroomerChatList(Long customerId);

    // 미용사 채팅방 목록 검색 조회
    List<GetGroomerChatListResponseDto> getGroomerChatListBySearchKeyword(GetGroomerKeywordChatListRequestDto requestDto);

}
