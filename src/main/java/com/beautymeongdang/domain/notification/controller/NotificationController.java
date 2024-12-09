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
    @PostMapping
    public ResponseEntity<String> saveNotification(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long groomerId,
            @RequestParam String notifyType,
            @RequestParam String notifyContent,
            @RequestParam String link) {

        if (customerId != null) {
            notificationService.saveNotification(customerId, "customer", notifyType, notifyContent, link);
            return ResponseEntity.ok("Customer notification saved.");
        } else if (groomerId != null) {
            notificationService.saveNotification(groomerId, "groomer", notifyType, notifyContent, link);
            return ResponseEntity.ok("Groomer notification saved.");
        } else {
            return ResponseEntity.badRequest().body("Either customerId or groomerId must be provided.");
        }
    }

    // 알림 조회 API
    @GetMapping
    public ResponseEntity<List<Object>> getNotifications(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long groomerId) {

        if (customerId != null) {
            List<Object> notifications = notificationService.getNotifications(customerId, "customer");
            return ResponseEntity.ok(notifications);
        } else if (groomerId != null) {
            List<Object> notifications = notificationService.getNotifications(groomerId, "groomer");
            return ResponseEntity.ok(notifications);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 알림 읽음 처리 API
    @PatchMapping
    public ResponseEntity<String> markAsRead(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long groomerId,
            @RequestParam int index) {

        if (customerId != null) {
            notificationService.markAsRead(customerId, "customer", index);
            return ResponseEntity.ok("Customer notification marked as read.");
        } else if (groomerId != null) {
            notificationService.markAsRead(groomerId, "groomer", index);
            return ResponseEntity.ok("Groomer notification marked as read.");
        } else {
            return ResponseEntity.badRequest().body("Either customerId or groomerId must be provided.");
        }
    }

    // 알림 삭제 API
    @DeleteMapping
    public ResponseEntity<String> clearNotifications(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long groomerId) {

        if (customerId != null) {
            notificationService.clearNotifications(customerId, "customer");
            return ResponseEntity.ok("Customer notifications cleared.");
        } else if (groomerId != null) {
            notificationService.clearNotifications(groomerId, "groomer");
            return ResponseEntity.ok("Groomer notifications cleared.");
        } else {
            return ResponseEntity.badRequest().body("Either customerId or groomerId must be provided.");
        }
    }
}
