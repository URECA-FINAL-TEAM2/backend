package com.beautymeongdang.domain.notification.controller;

import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 알림 조회 API
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        List<Object> notifications = notificationService.getNotifications(userId, "customer");
        return ResponseEntity.ok(Map.of("status", "success", "data", notifications));
    }

    // 읽지 않은 알림 개수 조회 API
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadNotificationCount(
            @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        int unreadCount = notificationService.getUnreadNotificationCount(userId, "customer");
        return ResponseEntity.ok(Map.of("status", "success", "unreadCount", unreadCount));
    }

    // 특정 알림 삭제 API
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable String notificationId) {
        Long userId = extractUserIdFromToken(token);
        notificationService.deleteNotification(userId, "customer", notificationId);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Notification deleted."));
    }

    // 전체 알림 삭제 API
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearNotifications(
            @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);
        notificationService.clearAllNotifications(userId, "customer");
        return ResponseEntity.ok(Map.of("status", "success", "message", "All notifications cleared."));
    }

    // JWT에서 userId 추출
    private Long extractUserIdFromToken(String token) {
        // JWT 파싱 로직 예시
        try {
            // JWT를 실제로 파싱하는 유틸리티 또는 라이브러리 사용
            // 예: Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            // String userId = claims.getBody().get("userId", String.class);
            // return Long.parseLong(userId);

            // 아래는 테스트용으로 고정된 값 반환
            return 123L; // 실제 사용자 ID 반환
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token.");
        }
    }
}