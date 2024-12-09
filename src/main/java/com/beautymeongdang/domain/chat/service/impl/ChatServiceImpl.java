package com.beautymeongdang.domain.chat.service.impl;

import com.beautymeongdang.domain.chat.dto.GetCustomerChatListResponseDto;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    // 고객 채팅방 목록 조회
    @Override
    public List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId) {
        return chatRepository.getCustomerChatList(customerId);
    }
}
