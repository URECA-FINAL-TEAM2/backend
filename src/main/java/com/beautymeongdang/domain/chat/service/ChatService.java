package com.beautymeongdang.domain.chat.service;

import com.beautymeongdang.domain.chat.dto.GetCustomerChatListResponseDto;

import java.util.List;

public interface ChatService {
    // 고객 채팅방 목록 조회
    List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId);
}
