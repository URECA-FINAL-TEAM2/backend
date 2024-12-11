package com.beautymeongdang.domain.notification.service.impl;

import com.beautymeongdang.domain.notification.controller.NotificationController;
import com.beautymeongdang.domain.notification.repository.NotificationRepository;
import com.beautymeongdang.domain.notification.service.NotificationEventPublisher;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEventPublisher notificationEventPublisher;
    private final ObjectMapper objectMapper;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationEventPublisher notificationEventPublisher,
            ObjectMapper objectMapper
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationEventPublisher = notificationEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveNotification(Long userId, String roleType, String notifyType, String notifyContent) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", UUID.randomUUID().toString());
        notification.put("userId", userId);
        notification.put("roleType", roleType);
        notification.put("notifyType", notifyType);
        notification.put("content", notifyContent);
        notification.put("readCheckYn", false);
        notification.put("createdAt", System.currentTimeMillis());

        notificationRepository.saveNotification(userId, roleType, notification);

        sendRealTimeNotification(userId, roleType, notifyContent);
    }


    private void sendRealTimeNotification(Long userId, String roleType, String message) {
        notificationEventPublisher.publishNotification(userId, roleType, message);
    }

    @Override
    public List<Object> getNotifications(Long userId, String roleType) {
        List<Object> notifications = notificationRepository.getNotifications(userId, roleType);
        return notifications.stream()
                .map(notification -> objectMapper.convertValue(notification, Map.class))
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadNotificationCount(Long userId, String roleType) {
        return notificationRepository.getUnreadNotificationCount(userId, roleType);
    }

    @Override
    public void deleteNotification(Long userId, String roleType, String notificationId) {
        notificationRepository.deleteNotificationById(userId, roleType, notificationId);
    }

    @Override
    public void clearAllNotifications(Long userId, String roleType) {
        notificationRepository.clearNotifications(userId, roleType);
    }
}
