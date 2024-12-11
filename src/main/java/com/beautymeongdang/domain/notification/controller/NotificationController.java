package com.beautymeongdang.domain.notification.controller;

import com.beautymeongdang.domain.notification.service.NotificationEventPublisher;
import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationEventPublisher notificationEventPublisher;

    public NotificationController(
            NotificationService notificationService,
            NotificationEventPublisher notificationEventPublisher
    ) {
        this.notificationService = notificationService;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    // SSE 연결
    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam("userId") Long userId,
                              @RequestParam("roleType") String roleType) {
        String key = String.format("%d:%s", userId, roleType);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        notificationEventPublisher.addEmitter(key, emitter);

        emitter.onCompletion(() -> notificationEventPublisher.removeEmitter(key));
        emitter.onTimeout(() -> notificationEventPublisher.removeEmitter(key));
        emitter.onError((e) -> notificationEventPublisher.removeEmitter(key));

        return emitter;
    }

    // 알림 조회 API
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType) {
        List<Object> notifications = notificationService.getNotifications(userId, roleType);
        return ResponseEntity.ok(Map.of("status", "success", "data", notifications));
    }

    // 읽지 않은 알림 개수 조회 API
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadNotificationCount(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType) {
        int unreadCount = notificationService.getUnreadNotificationCount(userId, roleType);
        return ResponseEntity.ok(Map.of("status", "success", "unreadCount", unreadCount));
    }

    // 특정 알림 삭제 API
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType,
            @PathVariable String notificationId) {
        notificationService.deleteNotification(userId, roleType, notificationId);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Notification deleted."));
    }

    // 전체 알림 삭제 API
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> clearNotifications(
            @RequestParam("userId") Long userId,
            @RequestParam("roleType") String roleType) {
        notificationService.clearAllNotifications(userId, roleType);
        return ResponseEntity.ok(Map.of("status", "success", "message", "All notifications cleared."));
    }
}
