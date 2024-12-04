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

    // 알림 저장 API
    @PostMapping("/{userId}")
    public ResponseEntity<String> saveNotification(
            @PathVariable Long userId,
            @RequestParam String roleType,
            @RequestParam String notifyType,
            @RequestParam String notifyContent,
            @RequestParam String link) {
        notificationService.saveNotification(userId, roleType, notifyType, notifyContent, link);
        return ResponseEntity.ok("Notification saved.");
    }

    // 알림 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<List<Object>> getNotifications(
            @PathVariable Long userId,
            @RequestParam String roleType) {
        List<Object> notifications = notificationService.getNotifications(userId, roleType);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리 API
    @PatchMapping("/{userId}")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long userId,
            @RequestParam String roleType,
            @RequestParam int index) {
        notificationService.markAsRead(userId, roleType, index);
        return ResponseEntity.ok("Notification marked as read.");
    }

    // 알림 삭제 API
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> clearNotifications(
            @PathVariable Long userId,
            @RequestParam String roleType) {
        notificationService.clearNotifications(userId, roleType);
        return ResponseEntity.ok("Notifications cleared.");
    }
}
