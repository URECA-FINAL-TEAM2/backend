package com.beautymeongdang.domain.notification.service.impl;

import com.beautymeongdang.domain.notification.repository.NotificationRepository;
import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
    }

    @Override
    public List<Object> getNotifications(Long userId, String roleType) {
        return notificationRepository.getNotifications(userId, roleType);
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