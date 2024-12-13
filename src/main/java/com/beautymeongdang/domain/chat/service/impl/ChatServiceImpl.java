package com.beautymeongdang.domain.chat.service.impl;

import com.beautymeongdang.domain.chat.dto.*;
import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.chat.service.ChatService;
import com.beautymeongdang.domain.notification.enums.NotificationType;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.exception.handler.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;
    private final GroomerRepository groomerRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationService notificationService;


    /**
     * 채팅방 생성
     */
    @Override
    @Transactional
    public CreateChatResponseDto createChat(CreateChatRequestDto request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

        Groomer groomer = groomerRepository.findById(request.getGroomerId())
                .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

        // 같은 사용자인지 확인 (User ID 같은 사람끼리 채팅 X)
        if (customer.getUserId().getUserId().equals(groomer.getUserId().getUserId())) {
            throw BadRequestException.invalidRequest("같은 사용자와는 채팅할 수 없습니다.");
        }

        Optional<Chat> existingChat = chatRepository.findByCustomerIdAndGroomerIdAndNotDeleted(customer, groomer);

        if (existingChat.isPresent()) {
            Chat chat = existingChat.get();
            if (chat.getCustomerExitedYn() || chat.getGroomerExitedYn()) {
                // 둘 중 한 명이라도 퇴장한 방이면 새로운 채팅방 생성
                Chat newChat = Chat.builder()
                        .customerId(customer)
                        .groomerId(groomer)
                        .customerExitedYn(false)
                        .groomerExitedYn(false)
                        .build();

                Chat savedChat = chatRepository.save(newChat);
                return CreateChatResponseDto.from(savedChat);
            } else {
                // 아무도 퇴장하지 않은 채팅방이 있으면
                throw BadRequestException.invalidRequest("채팅방이 이미 존재합니다.");
            }
        }

        Chat newChat = Chat.builder()
                .customerId(customer)
                .groomerId(groomer)
                .customerExitedYn(false)
                .groomerExitedYn(false)
                .build();

        Chat savedChat = chatRepository.save(newChat);

        // 알림 저장 로직 추가
        String notificationMessage = String.format(
                "새 채팅방이 생성되었습니다. 고객: %s",
                customer.getUserId().getUserName()
        );

        // 알림 저장
        notificationService.saveNotification(
                groomer.getUserId().getUserId(),
                "groomer",
                NotificationType.CHAT_ROOM.getDescription(),
                notificationMessage
        );

        return CreateChatResponseDto.from(savedChat);
    }




    /**
     *  채팅방 조회 및 접근 권한 검증을 동시에 수행
     */
    @Override
    public void validateChatRoomAccess(Long chatId, Long userId, Boolean customerYn) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> NotFoundException.entityNotFound("채팅방"));

        if (customerYn) {
            User user = User.builder().userId(userId).build();

            if (!chat.getCustomerId().getUserId().getUserId().equals(userId)) {
                throw UnauthorizedException.invalidAccess("해당 채팅방에 접근 권한이 없습니다.");
            }

            // 고객 본인이 퇴장한 경우만 체크
            if (chat.getCustomerExitedYn()) {
                throw UnauthorizedException.invalidAccess("이미 나간 채팅방입니다.");
            }

        } else {
            User user = User.builder().userId(userId).build();

            if (!chat.getGroomerId().getUserId().getUserId().equals(userId)) {
                throw UnauthorizedException.invalidAccess("해당 채팅방에 접근 권한이 없습니다.");
            }

            // 미용사 본인이 퇴장한 경우만 체크
            if (chat.getGroomerExitedYn()) {
                throw UnauthorizedException.invalidAccess("이미 나간 채팅방입니다.");
            }
        }
    }

    /**
     * 채팅방 나가기
     */
    @Override
    @Transactional
    public UpdateChatExitResponseDto exitChat(Long chatId, Long userId, Boolean customerYn) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> NotFoundException.entityNotFound("채팅방"));

        validateChatRoomAccess(chatId, userId, customerYn);

        if (customerYn) {
            chat.customerExited();
        } else {
            chat.groomerExited();
        }

        // 양쪽 모두 퇴장했는지 확인하고 채팅방 논리적 삭제
        if (chat.getCustomerExitedYn() && chat.getGroomerExitedYn()) {
            chat.delete();
        }

        // 퇴장 메시지 전송
        String destination = "/sub/chat/room/" + chat.getChatId();
        CreateChatMessageResponseDto quitMessage = CreateChatMessageResponseDto.builder()
                .chatId(chat.getChatId())
                .senderId(userId)
                .messageType(ChatMessage.MessageType.QUIT)
                .content("상대방이 퇴장하였습니다.")
                .customerYn(customerYn)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend(destination, quitMessage);

        return UpdateChatExitResponseDto.from(chat, userId, customerYn);
    }


    // 고객 채팅방 목록 조회
    @Override
    public List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId) {
        return chatRepository.getCustomerChatList(customerId);
    }

    // 고객 채팅방 목록 검색 조회
    @Override
    public List<GetCustomerChatListResponseDto> getCustomerChatListBySearchKeyword(Long customerId, String searchWord) {
        return chatRepository.getCustomerChatListBySearchKeyword(customerId, searchWord);
    }

    // 미용사 채팅방 목록 조회
    @Override
    public List<GetGroomerChatListResponseDto> getGroomerChatList(Long customerId) {
        return chatRepository.getGroomerChatList(customerId);
    }

    // 미용사 채팅방 목록 검색 조회
    @Override
    public List<GetGroomerChatListResponseDto> getGroomerChatListBySearchKeyword(Long groomerId, String searchWord) {
        return chatRepository.getGroomerChatListBySearchKeyword(groomerId, searchWord);
    }
}
