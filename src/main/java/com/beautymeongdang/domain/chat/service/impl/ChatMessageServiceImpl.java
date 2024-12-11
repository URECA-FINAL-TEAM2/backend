package com.beautymeongdang.domain.chat.service.impl;


import com.beautymeongdang.domain.chat.dto.*;
import com.beautymeongdang.domain.chat.entity.Chat;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.chat.service.ChatMessageService;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;
    private final GroomerRepository groomerRepository;
    private final FileStore fileStore;
    private final ChatMessageImageRepository chatMessageImageRepository;


    /**
     * 메시지 전송
     */
    @Override
    @Transactional
    public CreateChatMessageResponseDto sendMessage(CreateChatMessageRequestDto messageRequestDto) {
        Chat chat = chatRepository.findById(messageRequestDto.getChatId())
                .orElseThrow(() -> NotFoundException.entityNotFound("채팅방"));

        // 발신자 정보 조회
        String senderNickname;
        String senderProfileImage;
        if (messageRequestDto.getCustomerYn()) {
            Customer customer = customerRepository.findById(messageRequestDto.getSenderId())
                    .orElseThrow(() -> NotFoundException.entityNotFound("고객"));
            senderNickname = customer.getUserId().getNickname();
            senderProfileImage = customer.getUserId().getProfileImage();
        } else {
            Groomer groomer = groomerRepository.findById(messageRequestDto.getSenderId())
                    .orElseThrow(() -> NotFoundException.entityNotFound("미용사"));
            senderNickname = groomer.getUserId().getNickname();
            senderProfileImage = groomer.getUserId().getProfileImage();
        }

        String imageUrl = null;
        if (StringUtils.hasText(messageRequestDto.getBase64Image())) {
            UploadedFile uploadedFile = fileStore.storeBase64File(
                    messageRequestDto.getBase64Image(),
                    FileStore.CHAT_IMAGES
            );
            imageUrl = uploadedFile.getFileUrl();
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatId(chat)
                .content(messageRequestDto.getContent())
                .customerYn(messageRequestDto.getCustomerYn())
                .messageType(messageRequestDto.getMessageType())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        if (imageUrl != null) {
            ChatMessageImage chatMessageImage = ChatMessageImage.builder()
                    .messageId(savedMessage)
                    .imageUrl(imageUrl)
                    .build();
            chatMessageImageRepository.save(chatMessageImage);
        }

        return CreateChatMessageResponseDto.builder()
                .chatId(savedMessage.getChatId().getChatId())
                .senderId(messageRequestDto.getSenderId())
                .senderNickname(senderNickname)
                .senderProfileImage(senderProfileImage)
                .content(savedMessage.getContent())
                .messageType(savedMessage.getMessageType())
                .customerYn(savedMessage.getCustomerYn())
                .imageUrl(imageUrl)
                .createdAt(savedMessage.getCreatedAt())
                .build();
    }

    // 채팅 조회
    @Override
    public GetChatMessageListResponseDto getChatMessageList(Long chatId) {
        // 미용사
        User groomer = chatRepository.findGroomerByChatId(chatId);

        // 고객
        User customer = chatRepository.findCustomerByChatId(chatId);

        // 채팅
        List<GetChatMessageResponseDto> chatMessageResponseDtoList = chatMessageRepository.findChatMessagesWithImages(chatId);

        GetChatMessageListResponseDto.GroomerInfo groomerInfo = GetChatMessageListResponseDto.GroomerInfo.builder()
                .groomerProfileImage(groomer.getProfileImage())
                .groomerName(groomer.getNickname())
                .build();

        GetChatMessageListResponseDto.CustomerInfo customerInfo = GetChatMessageListResponseDto.CustomerInfo.builder()
                .customerProfileImage(customer.getProfileImage())
                .customerName(customer.getUserName())
                .build();

        return GetChatMessageListResponseDto.builder()
                .groomerInfo(groomerInfo)
                .customerInfo(customerInfo)
                .messages(chatMessageResponseDtoList)
                .build();
    }


}