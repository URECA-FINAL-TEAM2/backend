package com.beautymeongdang.domain.chat.controller;

import com.beautymeongdang.domain.chat.dto.CreateChatMessageRequestDto;
import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;
import com.beautymeongdang.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     *  메시지 보내기
     */
    @MessageMapping("/send")
    public void sendMessage(@Payload CreateChatMessageRequestDto messageRequestDto) {
        log.info("메시지 전송 요청. chatId: {}, senderId: {}",
                messageRequestDto.getChatId(), messageRequestDto.getSenderId());

        CreateChatMessageResponseDto response = chatMessageService.sendMessage(messageRequestDto);
        messagingTemplate.convertAndSend("/sub/chat/room/" + messageRequestDto.getChatId(), response);

        log.info("메시지 전송 완료. chatId: {}", messageRequestDto.getChatId());
    }

}