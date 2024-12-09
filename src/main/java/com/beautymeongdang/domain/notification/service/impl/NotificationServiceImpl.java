package com.beautymeongdang.domain.notification.service.impl;

import com.beautymeongdang.domain.notification.repository.NotificationRepository;
import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void saveNotification(Long userId, String roleType, String notifyType, String notifyContent) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("notifyType", notifyType);
        notification.put("notifyContent", notifyContent);
        notification.put("readCheckYn", false);
        notification.put("createdAt", LocalDateTime.now().toString());

        notificationRepository.saveNotification(userId, roleType, notification);
    }

    @Override
    public List<Object> getNotifications(Long userId) {
        List<Object> customerNotifications = notificationRepository.getNotifications(userId, "customer");
        List<Object> groomerNotifications = notificationRepository.getNotifications(userId, "groomer");

        customerNotifications.addAll(groomerNotifications);
        return customerNotifications;
    }

    @Override
    public void markAsReadById(Long userId, String roleType, String notificationId) {
        notificationRepository.markAsReadById(userId, roleType, notificationId);
    }

    @Override
    public void clearNotifications(Long userId, String roleType) {
        notificationRepository.clearNotifications(userId, roleType);
    }

    @Override
    public int getUnreadNotificationCount(Long userId, String roleType) {
        return notificationRepository.getUnreadNotificationCount(userId, roleType);
    }
}
