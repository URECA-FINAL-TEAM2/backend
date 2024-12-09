package com.beautymeongdang.domain.chat.controller;


import com.beautymeongdang.domain.chat.service.ChatService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 고객 채팅방 목록 조회
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerChatList(@PathVariable Long customerId) {
        return ApiResponse.ok(200, chatService.getCustomerChatList(customerId), "Get CustomerChatRoomList Success");
    }
}
