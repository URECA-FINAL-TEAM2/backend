package com.beautymeongdang.domain.notification.service.impl;

import com.beautymeongdang.domain.notification.service.NotificationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final RedisTemplate<String, Object> redisTemplate;

    public NotificationServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 공통 코드 검증
    private void validateRoleType(String roleType) {
        if (!roleType.equals("customer") && !roleType.equals("groomer")) {
            throw new IllegalArgumentException("Invalid role type: " + roleType);
        }
    }

    @Override
    public void saveNotification(Long userId, String roleType, String notifyType, String notifyContent, String link) {
        validateRoleType(roleType);
        String key = String.format("notifications:%d:%s", userId, roleType);

        Map<String, Object> notification = new HashMap<>();
        notification.put("notifyType", notifyType);
        notification.put("notifyContent", notifyContent);
        notification.put("link", link);
        notification.put("readCheckYn", false);
        notification.put("createdAt", LocalDateTime.now().toString());

        redisTemplate.opsForList().rightPush(key, notification);
    }

    @Override
    public List<Object> getNotifications(Long userId, String roleType) {
        validateRoleType(roleType); // 역할 검증
        String key = String.format("notifications:%d:%s", userId, roleType);
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void markAsRead(Long userId, String roleType, int index) {
        validateRoleType(roleType);
        String key = String.format("notifications:%d:%s", userId, roleType);
        List<Object> notifications = redisTemplate.opsForList().range(key, 0, -1);

        if (notifications != null && index < notifications.size()) {
            Map<String, Object> notification = (Map<String, Object>) notifications.get(index);
            notification.put("readCheckYn", true);
            redisTemplate.opsForList().set(key, index, notification); // Redis 데이터 업데이트
        } else {
            throw new IllegalArgumentException("Invalid notification index.");
        }
    }

    @Override
    public void clearNotifications(Long userId, String roleType) {
        validateRoleType(roleType);
        String key = String.format("notifications:%d:%s", userId, roleType);
        redisTemplate.delete(key);
    }
}
