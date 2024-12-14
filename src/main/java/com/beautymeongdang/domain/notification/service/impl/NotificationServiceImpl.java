package com.beautymeongdang.domain.notification.service.impl;

import com.beautymeongdang.domain.notification.repository.NotificationRepository;
import com.beautymeongdang.domain.notification.service.NotificationEmailService;
import com.beautymeongdang.domain.notification.service.NotificationEventPublisher;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

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
    private final NotificationEmailService notificationEmailService;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationEventPublisher notificationEventPublisher,
            ObjectMapper objectMapper,
            NotificationEmailService notificationEmailService,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationEventPublisher = notificationEventPublisher;
        this.objectMapper = objectMapper;
        this.notificationEmailService = notificationEmailService;
        this.userRepository = userRepository;
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

        sendEmailNotification(userId, notifyType, notifyContent);
    }


    private void sendRealTimeNotification(Long userId, String roleType, String message) {
        notificationEventPublisher.publishNotification(userId, roleType, message);
    }

    private void sendEmailNotification(Long userId, String notifyType, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        String userName = user.getUserName();
        String userEmail = user.getEmail();

        if (!"예약 알림".equals(notifyType) && !"예약 취소 알림".equals(notifyType)) {
            return;
        }

        String subject = String.format("미용멍당 - 새로운 %s", notifyType);

        // 템플릿 변수 설정
        Map<String, Object> variables = Map.of(
                "userName", userName,
                "notifyType", notifyType,
                "content", content
        );

        notificationEmailService.sendEmail(userEmail, subject, "email", variables);
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
