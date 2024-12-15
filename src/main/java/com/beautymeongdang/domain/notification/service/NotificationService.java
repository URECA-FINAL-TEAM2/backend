package com.beautymeongdang.domain.notification.service;

import java.util.List;

public interface NotificationService {
    void saveNotification(Long userId, String roleType, String notifyType, String notifyContent);
    List<Object> getNotifications(Long userId, String roleType);
    int getUnreadNotificationCount(Long userId, String roleType);
    void deleteNotification(Long userId, String roleType, String notificationId);
    void clearAllNotifications(Long userId, String roleType);
    void markAsRead(Long userId, String roleType, String notificationId, boolean isRead);
}