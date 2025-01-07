package com.beautymeongdang.domain.chat.controller;

import com.beautymeongdang.domain.chat.dto.*;
import com.beautymeongdang.domain.chat.pubsub.RedisPublisher;
import com.beautymeongdang.domain.chat.service.ChatMessageService;
import com.beautymeongdang.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;
    //private final SimpMessageSendingOperations messagingTemplate;


    /**
     *  메시지 보내기
     */
    @MessageMapping("/send")
    public void sendMessage(@Payload CreateChatMessageRequestDto messageRequestDto) {
        CreateChatMessageResponseDto response = chatMessageService.sendMessage(messageRequestDto);
        //messagingTemplate.convertAndSend("/sub/chat/room/" + messageRequestDto.getChatId(), response);
         redisPublisher.publish(response);
    }


    // 채팅 조회
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<GetChatMessageListResponseDto>> getChatMessageList(@PathVariable("chatId") Long chatId) {
        return ApiResponse.ok(200, chatMessageService.getChatMessageList(chatId), "Get Message success");
    }

    // 채팅 논리적 삭제
    @PutMapping("/{messageId}")
    public ResponseEntity<ApiResponse<DeleteChatMessageResponseDto>> deleteChatMessage(@PathVariable("messageId") Long messageId) {
        return ApiResponse.ok(200, chatMessageService.deleteChatMessage(messageId), "Delete Message Success");
    }


    /**
     * 안 읽은 메시지 수
     */
    @GetMapping("/unread/{chatId}")
    public ResponseEntity<ApiResponse<GetUnreadMessageCountResponseDto>> getUnreadMessageCount(@PathVariable Long chatId, @RequestParam Long userId) {
        GetUnreadMessageCountResponseDto response = chatMessageService.getUnreadMessageCount(chatId, userId);
        return ApiResponse.ok(200, response, "안 읽은 메시지 수 조회 성공");
    }


    /**
     * 메시지 읽음 처리
     */
    @MessageMapping("/read")
    public void markAsRead(@Payload UpdateMessageReadRequestDto request) {
        chatMessageService.markMessagesAsRead(request.getChatId(), request.getSenderId());
    }

}