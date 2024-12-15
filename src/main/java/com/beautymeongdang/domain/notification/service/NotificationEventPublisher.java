package com.beautymeongdang.domain.notification.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationEventPublisher {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String key, SseEmitter emitter) {
        emitters.put(key, emitter);
    }

    public void removeEmitter(String key) {
        emitters.remove(key);
    }

    @Async
    public void publishNotification(Long userId, String roleType, String message) {
        String key = String.format("%d:%s", userId, roleType);
        SseEmitter emitter = emitters.get(key);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (IOException e) {
                emitters.remove(key);
            }
        }
    }
}