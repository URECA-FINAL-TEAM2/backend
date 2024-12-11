package com.beautymeongdang.domain.chat.service.impl;

import com.beautymeongdang.domain.chat.dto.*;
import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.chat.service.ChatService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.global.exception.handler.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;
    private final GroomerRepository groomerRepository;


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

        chatRepository.findByCustomerIdAndGroomerIdAndNotDeleted(customer, groomer)
                .ifPresent(chat -> {
                    throw BadRequestException.invalidRequest("채팅방이 이미 존재합니다.");
                });

        Chat newChat = Chat.builder()
                .customerId(customer)
                .groomerId(groomer)
                .customerExitedYn(false)
                .groomerExitedYn(false)
                .build();

        Chat savedChat = chatRepository.save(newChat);

        return CreateChatResponseDto.from(savedChat);
    }


    /**
     *  채팅방 조회 및 접근 권한 검증을 동시에 수행
     */
    @Override
    public void validateChatRoomAccess(Long chatId, Long userId, Boolean customerYn) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> NotFoundException.entityNotFound("채팅방"));

        // 권한 확인
        if (customerYn) {
            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> NotFoundException.entityNotFound("고객"));

            if (!chat.getCustomerId().getCustomerId().equals(customer.getCustomerId())) {
                throw UnauthorizedException.invalidAccess("해당 채팅방에 접근 권한이 없습니다.");
            }

            if (chat.getCustomerExitedYn()) {
                throw UnauthorizedException.invalidAccess("이미 나간 채팅방입니다.");
            }
        } else {
            Groomer groomer = groomerRepository.findById(userId)
                    .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));

            if (!chat.getGroomerId().getGroomerId().equals(groomer.getGroomerId())) {
                throw UnauthorizedException.invalidAccess("해당 채팅방에 접근 권한이 없습니다.");
            }

            if (chat.getGroomerExitedYn()) {
                throw UnauthorizedException.invalidAccess("이미 나간 채팅방입니다.");
            }
        }
    }

    // 고객 채팅방 목록 조회
    @Override
    public List<GetCustomerChatListResponseDto> getCustomerChatList(Long customerId) {
        return chatRepository.getCustomerChatList(customerId);
    }

    // 고객 채팅방 목록 검색 조회
    @Override
    public List<GetCustomerChatListResponseDto> getCustomerChatListBySearchKeyword(GetCustomerKeywordChatListRequestDto requestDto) {
        return chatRepository.getCustomerChatListBySearchKeyword(requestDto.getCustomerId(), requestDto.getSearchWord());
    }

    // 미용사 채팅방 목록 조회
    @Override
    public List<GetGroomerChatListResponseDto> getGroomerChatList(Long customerId) {
        return chatRepository.getGroomerChatList(customerId);
    }

    // 미용사 채팅방 목록 검색 조회
    @Override
    public List<GetGroomerChatListResponseDto> getGroomerChatListBySearchKeyword(GetGroomerKeywordChatListRequestDto requestDto) {
        return chatRepository.getGroomerChatListBySearchKeyword(requestDto.getGroomerId(), requestDto.getSearchWord());
    }
}
