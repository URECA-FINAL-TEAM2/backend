package com.beautymeongdang.domain.notification.controller;

import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/sse-notifications")
public class SseNotificationController {

    private final NotificationService notificationService;

    public SseNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결
    @GetMapping("/{userId}")
    public SseEmitter connect(@PathVariable Long userId, @RequestParam String roleType) {
        // 역할 검증은 Service 내부에서 처리
        String key = String.format("%d:%s", userId, roleType);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(key, emitter);

        emitter.onCompletion(() -> emitters.remove(key));
        emitter.onTimeout(() -> emitters.remove(key));
        emitter.onError((e) -> emitters.remove(key));

        return emitter;
    }

    // 실시간 알림 전송
    @PostMapping("/{userId}")
    public ResponseEntity<String> sendNotification(
            @PathVariable Long userId,
            @RequestParam String roleType,
            @RequestParam String message) {

        String key = String.format("%d:%s", userId, roleType);
        SseEmitter emitter = emitters.get(key);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
                return ResponseEntity.ok("Notification sent.");
            } catch (IOException e) {
                emitters.remove(key); // Emitter 제거
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending notification.");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not connected.");
    }
}
