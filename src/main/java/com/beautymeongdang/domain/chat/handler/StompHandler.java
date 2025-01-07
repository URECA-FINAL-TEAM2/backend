package com.beautymeongdang.domain.chat.handler;


import com.beautymeongdang.domain.chat.dto.CreateChatMessageResponseDto;
import com.beautymeongdang.domain.chat.entity.ChatMessage;
import com.beautymeongdang.domain.chat.pubsub.RedisPublisher;
import com.beautymeongdang.domain.chat.service.ChatMessageService;
import com.beautymeongdang.domain.chat.service.ChatService;
import com.beautymeongdang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    //private final JWTUtil jwtUtil;
    private final ObjectProvider<ChatService> chatServiceProvider;
    private final ObjectProvider<ChatMessageService> chatMessageServiceProvider;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;


    private ChatService getChatService() {return chatServiceProvider.getObject();}
    private ChatMessageService getChatMessageService() {return chatMessageServiceProvider.getObject();}

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);


        // CONNECT: 사용자 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("연결 시도 - Headers: {}", accessor.toNativeHeaderMap());
            log.info("SessionId: {}", accessor.getSessionId());
            log.info("Headers: {}", accessor.toNativeHeaderMap());

//            인증 처리 잠시 해제
//            String authToken = extractToken(accessor);
//            log.debug("Extracted token: {}", authToken != null ? "exists" : "null");
//
//
//            if (!StringUtils.hasText(authToken)) {
//                log.error("[웹소켓 연결 실패] 토큰이 없습니다. sessionId: {}", accessor.getSessionId());
//                throw new RuntimeException("UNAUTHORIZED");
//            }
//
//            if (jwtUtil.isExpired(authToken)) {
//                log.error("[웹소켓 연결 실패] 만료된 토큰입니다. sessionId: {}", accessor.getSessionId());
//                throw new RuntimeException("TOKEN_EXPIRED");
//            }


            String customerYnStr = accessor.getFirstNativeHeader("CustomerYn");
            if (customerYnStr == null) {
                log.error("[웹소켓 연결 실패] CustomerYn 값이 없습니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("CUSTOMER_YN_REQUIRED");
            }

            String userId = accessor.getFirstNativeHeader("UserId");
            if (userId == null) {
                log.error("[웹소켓 연결 실패] UserId 값이 없습니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("USER_ID_REQUIRED");
            }


            // 세션에 사용자 정보 저장
            accessor.getSessionAttributes().put("CustomerYn", customerYnStr);
            accessor.getSessionAttributes().put("UserId", userId);
            accessor.setUser(() -> userId);


            log.info("[CONNECT] 현재 세션 정보: {}", accessor.getSessionAttributes());
            log.info("[웹소켓 연결 성공] sessionId: {}, userId: {}, customerYn: {}",
                    accessor.getSessionId(), userId, customerYnStr);
        }


         // SUBSCRIBE: 채팅방 입장 처리
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            log.info("[CONNECT] 현재 세션 정보: {}", accessor.getSessionAttributes());
            log.info("=== 구독 시도 ===");
            log.info("SessionId: {}", accessor.getSessionId());
            log.info("Destination: {}", accessor.getDestination());
            log.info("Session Attributes: {}", accessor.getSessionAttributes());


            String destination = accessor.getDestination();
            if (destination == null) {
                log.error("[구독 실패] 구독 대상이 없습니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("잘못된 구독 요청입니다");
            }

            String[] splits = destination.split("/");


            // 세션에서 사용자 정보 가져오기
            String customerYnStr = (String) accessor.getSessionAttributes().get("CustomerYn");
            String userId = (String) accessor.getSessionAttributes().get("UserId");
            if (customerYnStr == null || userId == null) {
                log.error("[구독 실패] 세션 정보가 없습니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("세션 정보가 없습니다");
            }

            // chatId(채팅방 Id) 추출 및 권한 검증
            Long chatId;
            try {
                chatId = Long.parseLong(splits[4]);
            } catch (NumberFormatException e) {
                log.error("[구독 실패] 잘못된 채팅방 ID입니다. destination: {}", destination);
                throw new RuntimeException("잘못된 채팅방 ID입니다");
            }


            // 읽음 처리 구독인 경우(/sub/chat/room/{chatId}/read)
            if (splits.length == 6 && splits[5].equals("read")) {
                handleReadSubscription(chatId, userId, customerYnStr);
            } else {
                // 일반 채팅방 구독인 경우(/sub/chat/room/{chatId})
                handleChatRoomSubscription(chatId, userId, customerYnStr);
            }

            log.info("⭕ [구독 성공] sessionId: {}, destination: {}, userId: {}",
                    accessor.getSessionId(), destination, userId);
        }

         // DISCONNECT: 웹소켓 연결 종료
        else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String userId = (String) accessor.getSessionAttributes().get("UserId");
            log.info("[웹소켓 연결 종료] sessionId: {}, userId: {}",
                    accessor.getSessionId(),
                    userId);
        }


        return message;
    }


//    private String extractToken(StompHeaderAccessor accessor) {
//        String bearerToken = accessor.getFirstNativeHeader("Authorization");
//        log.debug("Raw Authorization header: {}", bearerToken);
//
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//
//            String token = bearerToken.substring(7);
//            log.debug("Extracted token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
//
//            return bearerToken.substring(7);
//        }
//        log.warn("No valid bearer token found in headers");
//        return null;
//    }



    private void handleReadSubscription(Long chatId, String userId, String customerYnStr) {
        getChatService().validateChatRoomAccess(chatId, Long.parseLong(userId),
                Boolean.valueOf(customerYnStr));
        chatMessageService.markMessagesAsRead(chatId, Long.parseLong(userId));
    }


    private void handleChatRoomSubscription(Long chatId, String userId, String customerYnStr) {
        getChatService().validateChatRoomAccess(chatId, Long.parseLong(userId),
                Boolean.valueOf(customerYnStr));

        getChatMessageService().processUserEntrance(chatId, Long.parseLong(userId),
                Boolean.valueOf(customerYnStr));

        CreateChatMessageResponseDto readEvent = CreateChatMessageResponseDto.builder()
                .chatId(chatId)
                .senderId(Long.parseLong(userId))
                .messageType(ChatMessage.MessageType.TALK)
                .content("MESSAGE_READ")
                .customerYn(Boolean.valueOf(customerYnStr))
                .createdAt(LocalDateTime.now())
                .build();

        redisPublisher.publish(readEvent);
    }


}