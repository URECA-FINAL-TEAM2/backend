package com.beautymeongdang.domain.chat.controller;


import com.beautymeongdang.domain.chat.dto.*;
import com.beautymeongdang.domain.chat.service.ChatService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    /**
     * 채팅방 생성
     */
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequestDto request) {
        return ApiResponse.ok(200, chatService.createChat(request), "채팅방 생성 성공");
    }


    /**
     * 채팅방 퇴장
     */
    @PutMapping("/{chatId}/exit")
    public ResponseEntity<?> exitChat(
            @PathVariable Long chatId,
            @RequestBody UpdateChatExitRequestDto requestDto) {
        return ApiResponse.ok(200, chatService.exitChat(requestDto), "채팅방 나가기 성공");
    }


    // 고객 채팅방 목록 조회
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerChatList(@PathVariable Long customerId) {
        return ApiResponse.ok(200, chatService.getCustomerChatList(customerId), "Get CustomerChatRoomList Success");
    }

    // 고객 채팅방 목록 검색 조회
    @GetMapping("/customer")
    public ResponseEntity<?> getCustomerChatListBySearchKeyword(@RequestBody GetCustomerKeywordChatListRequestDto requestDto) {
        return ApiResponse.ok(200, chatService.getCustomerChatListBySearchKeyword(requestDto), "Get CustomerSearchWordChat Success");
    }

    // 미용사 채팅방 목록 조회
    @GetMapping("/groomer/{groomerId}")
    public ResponseEntity<?> getGroomerChatList(@PathVariable Long groomerId) {
        return ApiResponse.ok(200, chatService.getGroomerChatList(groomerId), "Get GroomerChatRoomList Success");
    }

    // 미용사 채팅방 목록 검색 조회
    @GetMapping("/groomer")
    public ResponseEntity<?> getGroomerChatListBySearchKeyword(@RequestBody GetGroomerKeywordChatListRequestDto requestDto) {
        return ApiResponse.ok(200, chatService.getGroomerChatListBySearchKeyword(requestDto), "Get GroomerSearchWordChat Success");
    }

}
