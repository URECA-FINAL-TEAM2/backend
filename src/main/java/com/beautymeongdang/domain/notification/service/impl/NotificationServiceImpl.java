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

    @Override
    public void saveNotification(Long id, String roleType, String notifyType, String notifyContent, String link) {
        String key = String.format("notifications:%d:%s", id, roleType);

        Map<String, Object> notification = new HashMap<>();
        notification.put("notifyType", notifyType);
        notification.put("notifyContent", notifyContent);
        notification.put("link", link);
        notification.put("readCheckYn", false);
        notification.put("createdAt", LocalDateTime.now().toString());

        redisTemplate.opsForList().rightPush(key, notification);
    }

    @Override
    public List<Object> getNotifications(Long id, String roleType) {
        String key = String.format("notifications:%d:%s", id, roleType);
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void markAsRead(Long id, String roleType, int index) {
        String key = String.format("notifications:%d:%s", id, roleType);
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
    public void clearNotifications(Long id, String roleType) {
        String key = String.format("notifications:%d:%s", id, roleType);
        redisTemplate.delete(key);
    }
}
