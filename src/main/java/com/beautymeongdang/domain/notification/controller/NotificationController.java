package com.beautymeongdang.domain.notification.controller;

import com.beautymeongdang.domain.notification.service.NotificationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
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

    private Long extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser() // parser() 메서드 사용
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // HMAC 키로 변환
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.get("userId", String.class);
            if (userId == null) {
                throw new IllegalArgumentException("userId not found in token.");
            }

            return Long.parseLong(userId);
        } catch (JwtException e) {
            // JWT 파싱 실패 시 처리
            throw new IllegalArgumentException("Invalid token.", e);
        } catch (NumberFormatException e) {
            // userId가 숫자로 변환되지 않을 경우 처리
            throw new IllegalArgumentException("Invalid userId format in token.", e);
        }
    }
}