package com.beautymeongdang.domain.notification.service.impl;

import com.beautymeongdang.domain.notification.service.NotificationEventPublisher;
import com.beautymeongdang.domain.notification.service.NotificationService;
import com.beautymeongdang.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationEventPublisher notificationEventPublisher;
//    private final NotificationEmailService notificationEmailService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public NotificationServiceImpl(
            NotificationEventPublisher notificationEventPublisher,
//            NotificationEmailService notificationEmailService,
            UserRepository userRepository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.notificationEventPublisher = notificationEventPublisher;
//        this.notificationEmailService = notificationEmailService;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveNotification(Long userId, String roleType, String notifyType, String notifyContent) {
        Map<String, Object> notification = new HashMap<>();
        String notificationId = UUID.randomUUID().toString();
        notification.put("id", notificationId);
        notification.put("userId", userId);
        notification.put("roleType", roleType);
        notification.put("notifyType", notifyType);
        notification.put("content", notifyContent);
        notification.put("readCheckYn", false);
        notification.put("createdAt", System.currentTimeMillis());

        // Redis에 알림 저장 및 TTL(2주) 설정
        String redisKey = "notifications:" + userId + ":" + roleType + ":" + notificationId;
        redisTemplate.opsForValue().set(redisKey, notification, 14, TimeUnit.DAYS);

        sendRealTimeNotification(userId, roleType, notifyContent);
//        sendEmailNotification(userId, notifyType, notifyContent);
    }

    private void sendRealTimeNotification(Long userId, String roleType, String message) {
        notificationEventPublisher.publishNotification(userId, roleType, message);
    }

//    private void sendEmailNotification(Long userId, String notifyType, String content) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
//
//        String userName = user.getUserName();
//        String userEmail = user.getEmail();
//
//        if (!"예약 알림".equals(notifyType) && !"예약 취소 알림".equals(notifyType)) {
//            return;
//        }
//
//        String subject = String.format("미용멍당 - 새로운 %s", notifyType);
//
//        // 템플릿 변수 설정
//        Map<String, Object> variables = Map.of(
//                "userName", userName,
//                "notifyType", notifyType,
//                "content", content
//        );
//
//        notificationEmailService.sendEmail(userEmail, subject, "email", variables);
//    }

    // 특정 알림 읽음처리
    @Override
    public void markAsRead(Long userId, String roleType, String notificationId, boolean isRead) {
        String redisKey = String.format("notifications:%d:%s:%s", userId, roleType, notificationId);
        @SuppressWarnings("unchecked")
        Map<String, Object> notification = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);

        if (notification != null) {
            Long ttl = redisTemplate.getExpire(redisKey);
            notification.put("readCheckYn", isRead);

            // TTL 유지하면서 업데이트
            if (ttl != null && ttl > 0) {
                redisTemplate.opsForValue().set(redisKey, notification, ttl, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(redisKey, notification, 14, TimeUnit.DAYS);
            }
        } else {
            throw new IllegalArgumentException("알림을 찾을 수 없습니다.");
        }
    }



    @Override
    public List<Object> getNotifications(Long userId, String roleType) {
        String keyPattern = "notifications:" + userId + ":" + roleType + ":*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        // 각 키의 값을 가져옴
        return keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadNotificationCount(Long userId, String roleType) {
        String keyPattern = "notifications:" + userId + ":" + roleType + ":*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys == null || keys.isEmpty()) {
            return 0;
        }

        // 읽지 않은 알림만 필터링
        return (int) keys.stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .filter(notification -> {
                    Map<String, Object> map = (Map<String, Object>) notification;
                    return map != null && Boolean.FALSE.equals(map.get("readCheckYn"));
                })
                .count();
    }

    @Override
    public void deleteNotification(Long userId, String roleType, String notificationId) {
        String redisKey = "notifications:" + userId + ":" + roleType + ":" + notificationId;
        redisTemplate.delete(redisKey);
    }

    @Override
    public void clearAllNotifications(Long userId, String roleType) {
        String keyPattern = "notifications:" + userId + ":" + roleType + ":*";
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
