package com.beautymeongdang.domain.chat.service.impl;


import com.beautymeongdang.domain.chat.dto.*;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.entity.ChatMessageMongo;
import com.beautymeongdang.domain.chat.pubsub.RedisPublisher;
import com.beautymeongdang.domain.chat.repository.ChatMessageImageRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageMongoRepository;
import com.beautymeongdang.domain.chat.repository.ChatMessageRepository;
import com.beautymeongdang.domain.chat.repository.ChatRepository;
import com.beautymeongdang.domain.chat.service.ChatMessageService;
import com.beautymeongdang.domain.shop.entity.Shop;
import com.beautymeongdang.domain.shop.repository.ShopRepository;
import com.beautymeongdang.domain.user.entity.Customer;
import com.beautymeongdang.domain.user.entity.Groomer;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.CustomerRepository;
import com.beautymeongdang.domain.user.repository.GroomerRepository;
import com.beautymeongdang.global.exception.handler.NotFoundException;
import com.beautymeongdang.infra.s3.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.beautymeongdang.global.common.entity.UploadedFile;
import com.beautymeongdang.domain.chat.entity.ChatMessageImage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


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
    private final ShopRepository shopRepository;
    private final ChatMessageMongoRepository chatMessageMongoRepository;
    private final RedisPublisher redisPublisher;


    /**
     * 메시지 전송
     */
    @Override
    @Transactional
    public CreateChatMessageResponseDto sendMessage(CreateChatMessageRequestDto messageRequestDto) {

        if ("MESSAGE_READ".equals(messageRequestDto.getContent())) {
            chatMessageMongoRepository.updateMessagesAsRead(
                    messageRequestDto.getChatId(),
                    messageRequestDto.getSenderId()
            );
            return CreateChatMessageResponseDto.builder()
                    .chatId(messageRequestDto.getChatId())
                    .senderId(messageRequestDto.getSenderId())
                    .messageType(messageRequestDto.getMessageType())
                    .content(messageRequestDto.getContent())
                    .customerYn(messageRequestDto.getCustomerYn())
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        // 발신자 정보 조회
        String senderNickname;
        String senderProfileImage;
        if (messageRequestDto.getCustomerYn()) {
            Customer customer = customerRepository.findByUserId(
                    User.builder().userId(messageRequestDto.getSenderId()).build()
            ).orElseThrow(() -> NotFoundException.entityNotFound("고객"));
            senderNickname = customer.getUserId().getNickname();
            senderProfileImage = customer.getUserId().getProfileImage();
        } else {
            Groomer groomer = groomerRepository.findByUserId(
                    User.builder().userId(messageRequestDto.getSenderId()).build()
            ).orElseThrow(() -> NotFoundException.entityNotFound("미용사"));
            senderNickname = groomer.getUserId().getNickname();
            senderProfileImage = groomer.getUserId().getProfileImage();
        }

//        s3 이미지
//        String imageUrl = null;
//        if (StringUtils.hasText(messageRequestDto.getBase64Image())) {
//            UploadedFile uploadedFile = fileStore.storeBase64File(
//                    messageRequestDto.getBase64Image(),
//                    FileStore.CHAT_IMAGES
//            );
//            imageUrl = uploadedFile.getFileUrl();
//        }
//
//        ChatMessage chatMessage = ChatMessage.builder()
//                .chatId(chat)
//                .content(messageRequestDto.getContent())
//                .customerYn(messageRequestDto.getCustomerYn())
//                .messageType(messageRequestDto.getMessageType())
//                .build();
//
//        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
//
//        if (imageUrl != null) {
//            ChatMessageImage chatMessageImage = ChatMessageImage.builder()
//                    .messageId(savedMessage)
//                    .imageUrl(imageUrl)
//                    .build();
//            chatMessageImageRepository.save(chatMessageImage);
//        }


        // MongoDB에 저장
        ChatMessageMongo mongoMessage = ChatMessageMongo.builder()
                .chatId(messageRequestDto.getChatId())
                .senderId(messageRequestDto.getSenderId())
                .content(messageRequestDto.getContent())
                .customerYn(messageRequestDto.getCustomerYn())
                .messageType(messageRequestDto.getMessageType())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessageMongo savedMessage = chatMessageMongoRepository.save(mongoMessage);
        log.info("MongoDB에 메시지 저장됨: {}", savedMessage);

        return CreateChatMessageResponseDto.builder()
                .chatId(savedMessage.getChatId())
                .senderId(savedMessage.getSenderId())
                .senderNickname(senderNickname)
                .senderProfileImage(senderProfileImage)
                .content(savedMessage.getContent())
                .messageType(savedMessage.getMessageType())
                .customerYn(savedMessage.getCustomerYn())
                .createdAt(savedMessage.getCreatedAt())
                .build();
    }




    // 채팅 조회 mongoDB
    @Override
    public GetChatMessageListResponseDto getChatMessageList(Long chatId) {
        User groomer = chatRepository.findGroomerByChatId(chatId);

        Shop shop = shopRepository.findShopsByChatId(chatId);

        User customer = chatRepository.findCustomerByChatId(chatId);

        List<ChatMessageMongo> mongoMessages = chatMessageMongoRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        log.info("MongoDB에서 조회한 메시지 수: {}", mongoMessages.size());

        // 채팅 메시지 조회 - MongoDB에서 조회하도록 변경
        List<GetChatMessageResponseDto> chatMessageResponseDtoList =
                chatMessageMongoRepository.findByChatIdOrderByCreatedAtAsc(chatId)
                        .stream()
                        .map(message -> GetChatMessageResponseDto.builder()
                                .messageId(message.getMessageId())
                                .content(message.getContent())
                                .customerYn(message.getCustomerYn())
                                .createdAt(message.getCreatedAt())
                                .build())
                        .collect(Collectors.toList());

        GetChatMessageListResponseDto.ShopInfo shopInfo = GetChatMessageListResponseDto.ShopInfo.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .address(shop.getAddress())
                .build();

        GetChatMessageListResponseDto.GroomerInfo groomerInfo = GetChatMessageListResponseDto.GroomerInfo.builder()
                .groomerProfileImage(groomer.getProfileImage())
                .groomerName(groomer.getNickname())
                .build();

        GetChatMessageListResponseDto.CustomerInfo customerInfo = GetChatMessageListResponseDto.CustomerInfo.builder()
                .customerProfileImage(customer.getProfileImage())
                .customerName(customer.getUserName())
                .build();

        return GetChatMessageListResponseDto.builder()
                .shopInfo(shopInfo)
                .groomerInfo(groomerInfo)
                .customerInfo(customerInfo)
                .messages(chatMessageResponseDtoList)
                .build();
    }



//    // 채팅 조회 (기존)
//    @Override
//    public GetChatMessageListResponseDto getChatMessageList(Long chatId) {
//        // 미용사
//        User groomer = chatRepository.findGroomerByChatId(chatId);
//
//        // 매장
//        Shop shop = shopRepository.findShopsByChatId(chatId);
//
//        // 고객
//        User customer = chatRepository.findCustomerByChatId(chatId);
//
//        // 채팅
//        List<GetChatMessageResponseDto> chatMessageResponseDtoList = chatMessageRepository.findChatMessagesWithImages(chatId);
//
//        GetChatMessageListResponseDto.ShopInfo shopInfo = GetChatMessageListResponseDto.ShopInfo.builder()
//                .shopId(shop.getShopId())
//                .shopName(shop.getShopName())
//                .address(shop.getAddress())
//                .build();
//
//        GetChatMessageListResponseDto.GroomerInfo groomerInfo = GetChatMessageListResponseDto.GroomerInfo.builder()
//                .groomerProfileImage(groomer.getProfileImage())
//                .groomerName(groomer.getNickname())
//                .build();
//
//        GetChatMessageListResponseDto.CustomerInfo customerInfo = GetChatMessageListResponseDto.CustomerInfo.builder()
//                .customerProfileImage(customer.getProfileImage())
//                .customerName(customer.getUserName())
//                .build();
//
//        return GetChatMessageListResponseDto.builder()
//                .shopInfo(shopInfo)
//                .groomerInfo(groomerInfo)
//                .customerInfo(customerInfo)
//                .messages(chatMessageResponseDtoList)
//                .build();
//    }

    // 채팅 논리적 삭제
    @Override
    @Transactional
    public DeleteChatMessageResponseDto deleteChatMessage(Long messageId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> NotFoundException.entityNotFound("메시지"));

        chatMessage.delete();

        return new DeleteChatMessageResponseDto(chatMessage.getMessageId());
    }


    /**
     * 일반적인 메시지 읽음 처리
     * MessageType: TALK
     */
    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        chatMessageMongoRepository.updateMessagesAsRead(chatId, userId);

        CreateChatMessageResponseDto readEvent = CreateChatMessageResponseDto.builder()
                .chatId(chatId)
                .senderId(userId)
                .messageType(ChatMessage.MessageType.TALK)
                .content("MESSAGE_READ")
                .build();

        redisPublisher.publish(readEvent);
    }


    /**
     *  채팅방 입장 시 기존 메시지 읽음 처리
     *  MessageType: ENTER
     */
    @Override
    @Transactional
    public void processUserEntrance(Long chatId, Long userId, Boolean isCustomer) {
        chatMessageMongoRepository.updateMessagesAsRead(chatId, userId);

        // 입장 이벤트 발행
        CreateChatMessageResponseDto enterMessage = CreateChatMessageResponseDto.builder()
                .chatId(chatId)
                .senderId(userId)
                .messageType(ChatMessage.MessageType.ENTER)
                .content("ENTERED")
                .customerYn(isCustomer)
                .createdAt(LocalDateTime.now())
                .build();

        redisPublisher.publish(enterMessage);
    }

    /**
     * 안 읽은 메시지 수
     */
    @Override
    public GetUnreadMessageCountResponseDto getUnreadMessageCount(Long chatId, Long userId) {
        Long unreadCount = chatMessageMongoRepository.countByChatIdAndIsReadFalseAndSenderIdNot(chatId, userId);

        return GetUnreadMessageCountResponseDto.builder()
                .chatId(chatId)
                .unreadCount(unreadCount)
                .build();
    }


}