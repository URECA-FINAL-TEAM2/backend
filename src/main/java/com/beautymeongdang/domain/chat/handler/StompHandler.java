package com.beautymeongdang.domain.chat.handler;


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

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final ObjectProvider<ChatService> chatServiceProvider;

    private ChatService getChatService() {
        return chatServiceProvider.getObject();
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);



        // CONNECT: 사용자 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = extractToken(accessor);
            if (!StringUtils.hasText(authToken)) {
                log.error("[웹소켓 연결 실패] 토큰이 없습니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("UNAUTHORIZED");
            }

            if (jwtUtil.isExpired(authToken)) {
                log.error("[웹소켓 연결 실패] 만료된 토큰입니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("TOKEN_EXPIRED");
            }

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
            accessor.setSessionAttributes(new HashMap<>());
            accessor.getSessionAttributes().put("CustomerYn", customerYnStr);
            accessor.getSessionAttributes().put("UserId", userId);
            accessor.setUser(() -> userId);


            log.info("[웹소켓 연결 성공] sessionId: {}, userId: {}, customerYn: {}",
                    accessor.getSessionId(), userId, customerYnStr);
        }


         // SUBSCRIBE: 채팅방 입장 처리
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination == null) {
                log.error("[구독 실패] 구독 대상이 없습니다. sessionId: {}", accessor.getSessionId());
                throw new RuntimeException("잘못된 구독 요청입니다");
            }

            String[] splits = destination.split("/");
            if (splits.length != 5) {
                log.error("[구독 실패] 잘못된 구독 경로입니다. destination: {}", destination);
                throw new RuntimeException("잘못된 구독 경로입니다");
            }

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

            try {
                getChatService().validateChatRoomAccess(chatId, Long.parseLong(userId), Boolean.valueOf(customerYnStr));
            } catch (Exception e) {
                log.error("[구독 실패] 채팅방 접근 권한이 없습니다. sessionId: {}, userId: {}, chatId: {}",
                        accessor.getSessionId(), userId, chatId);
                throw new RuntimeException("채팅방 접근 권한이 없습니다");
            }


            log.info("[구독 성공] sessionId: {}, destination: {}, userId: {}",
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


    private String extractToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}