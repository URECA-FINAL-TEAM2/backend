package com.beautymeongdang.domain.notification.controller;

import com.beautymeongdang.domain.notification.service.NotificationEventPublisher;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.beautymeongdang.global.exception.handler.UnauthorizedException;
import com.beautymeongdang.global.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationEventPublisher notificationEventPublisher;
    private final JwtProvider jwtProvider;

    public NotificationController(
            NotificationService notificationService,
            NotificationEventPublisher notificationEventPublisher,
            JwtProvider jwtProvider
    ) {
        this.notificationService = notificationService;
        this.notificationEventPublisher = notificationEventPublisher;
        this.jwtProvider = jwtProvider;
    }

    // SSE 연결
    @GetMapping("/connect")
    public SseEmitter connect(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType,
            @RequestHeader("Authorization") String accessToken,
            @CookieValue("access_token") String encryptedToken) {

        try {
            // Bearer 접두사 제거
            String token = accessToken.replace("Bearer ", "");

            // JWT 토큰 검증
            if (!jwtProvider.validateToken(encryptedToken, token)) {
                throw new UnauthorizedException("유효하지 않거나 만료된 토큰입니다");
            }

            // 토큰에서 추출한 사용자 ID와 제공된 userId 일치 여부 확인
            Long tokenUserId = jwtProvider.getUserIdFromToken(encryptedToken, token);
            if (!tokenUserId.equals(userId)) {
                throw new UnauthorizedException("토큰의 사용자 ID가 제공된 사용자 ID와 일치하지 않습니다");
            }

            String key = String.format("%d:%s", userId, roleType);

            SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
            notificationEventPublisher.addEmitter(key, emitter);

            emitter.onCompletion(() -> notificationEventPublisher.removeEmitter(key));
            emitter.onTimeout(() -> notificationEventPublisher.removeEmitter(key));
            emitter.onError((e) -> notificationEventPublisher.removeEmitter(key));

            return emitter;
        } catch (Exception e) {
            log.error("SSE connection error: {}", e.getMessage());
            throw new UnauthorizedException("SSE 연결 중 오류가 발생했습니다");
        }
    }

    // 알림 조회 API
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType,
            @RequestHeader("Authorization") String accessToken,
            @CookieValue("access_token") String encryptedToken) {

        validateToken(userId, accessToken, encryptedToken);
        List<Object> notifications = notificationService.getNotifications(userId, roleType);
        return ResponseEntity.ok(Map.of("status", "success", "data", notifications));
    }

    // 읽지 않은 알림 개수 조회 API
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadNotificationCount(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType,
            @RequestHeader("Authorization") String accessToken,
            @CookieValue("access_token") String encryptedToken) {

        validateToken(userId, accessToken, encryptedToken);
        int unreadCount = notificationService.getUnreadNotificationCount(userId, roleType);
        return ResponseEntity.ok(Map.of("status", "success", "unreadCount", unreadCount));
    }

    // 특정 알림 삭제 API
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType,
            @PathVariable String notificationId,
            @RequestHeader("Authorization") String accessToken,
            @CookieValue("access_token") String encryptedToken) {

        validateToken(userId, accessToken, encryptedToken);
        notificationService.deleteNotification(userId, roleType, notificationId);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Notification deleted."));
    }

    // 전체 알림 삭제 API
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearNotifications(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType,
            @RequestHeader("Authorization") String accessToken,
            @CookieValue("access_token") String encryptedToken) {

        validateToken(userId, accessToken, encryptedToken);
        notificationService.clearAllNotifications(userId, roleType);
        return ResponseEntity.ok(Map.of("status", "success", "message", "All notifications cleared."));
    }

    // 토큰 검증 유틸리티 메소드
    private void validateToken(Long userId, String accessToken, String encryptedToken) {
        try {
            String token = accessToken.replace("Bearer ", "");

            if (!jwtProvider.validateToken(encryptedToken, token)) {
                throw new UnauthorizedException("유효하지 않거나 만료된 토큰입니다");
            }

            Long tokenUserId = jwtProvider.getUserIdFromToken(encryptedToken, token);
            if (!tokenUserId.equals(userId)) {
                throw new UnauthorizedException("토큰의 사용자 ID가 제공된 사용자 ID와 일치하지 않습니다");
            }
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            throw new UnauthorizedException("토큰 검증 중 오류가 발생했습니다");
        }
    }
}