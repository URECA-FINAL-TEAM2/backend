package com.beautymeongdang.domain.notification.service;

import java.util.List;

public interface NotificationService {
    void saveNotification(Long userId, String roleType, String notifyType, String notifyContent);
    List<Object> getNotifications(Long userId);
    void markAsReadById(Long userId, String roleType, String notificationId); // 고유 ID 기반 읽음 처리
    void clearNotifications(Long userId, String roleType);
    int getUnreadNotificationCount(Long userId, String roleType);
}
