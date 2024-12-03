package com.beautymeongdang.domain.notification.service;

import java.util.List;

public interface NotificationService {

    // 알림 저장
    void saveNotification(Long userId, String roleType, String notifyType, String notifyContent, String link);

    // 알림 조회
    List<Object> getNotifications(Long userId, String roleType);

    // 알림 읽음 처리
    void markAsRead(Long userId, String roleType, int index);

    // 알림 삭제
    void clearNotifications(Long userId, String roleType);
}
