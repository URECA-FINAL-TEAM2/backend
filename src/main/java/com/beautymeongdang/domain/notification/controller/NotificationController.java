package com.beautymeongdang.domain.notification.controller;

import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 알림 조회 API (JWT에서 userId 추출)
    @GetMapping
    public ResponseEntity<List<Object>> getNotifications(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token); // JWT에서 userId 추출
        List<Object> notifications = notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리 API
    @PatchMapping
    public ResponseEntity<String> markAsRead(
            @RequestHeader("Authorization") String token,
            @RequestParam String index) { // 변경: int -> String

        Long userId = extractUserIdFromToken(token); // JWT에서 userId 추출
        try {
            int parsedIndex = Integer.parseInt(index); // String -> int 파싱
            notificationService.markAsRead(userId, parsedIndex);
            return ResponseEntity.ok("Notification marked as read.");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid index format.");
        }
    }

    // 알림 삭제 API
    @DeleteMapping
    public ResponseEntity<String> clearNotifications(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token); // JWT에서 userId 추출
        notificationService.clearNotifications(userId);
        return ResponseEntity.ok("Notifications cleared.");
    }

    // 읽지 않은 알림 개수 조회 API
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadNotificationCount(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token); // JWT에서 userId 추출
        int unreadCount = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(unreadCount);
    }

    // JWT에서 userId 추출
    private Long extractUserIdFromToken(String token) {
        // JWT 파싱 로직 구현 (예: Spring Security 또는 JWT 유틸리티 사용)
        // 예제: User user = jwtTokenProvider.getUserFromToken(token);
        // return user.getUserId();
        return 123L; // 예제 값
    }
}
