package com.beautymeongdang.domain.notification.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class NotificationRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public NotificationRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 알림 저장
    public void saveNotification(Long userId, String roleType, Map<String, Object> notification) {
        String key = getRedisKey(userId, roleType);
        redisTemplate.opsForHash().put(key, notification.get("id").toString(), notification);
    }

    // 알림 조회
    public List<Object> getNotifications(Long userId, String roleType) {
        String key = getRedisKey(userId, roleType);
        return redisTemplate.opsForHash().values(key);
    }

    // 특정 알림 읽음 처리 (고유 ID 기반)
    public void markAsReadById(Long userId, String roleType, String notificationId) {
        String key = getRedisKey(userId, roleType);
        @SuppressWarnings("unchecked")
        Map<String, Object> notification = (Map<String, Object>) redisTemplate.opsForHash().get(key, notificationId);

        if (notification != null) {
            notification.put("readCheckYn", true);
            redisTemplate.opsForHash().put(key, notificationId, notification);
        } else {
            throw new IllegalArgumentException("Notification not found.");
        }
    }

    // 알림 삭제
    public void clearNotifications(Long userId, String roleType) {
        String key = getRedisKey(userId, roleType);
        redisTemplate.delete(key);
    }

    // Redis 키 생성
    private String getRedisKey(Long userId, String roleType) {
        return String.format("notifications:%d:%s", userId, roleType);
    }

    // 읽지 않은 알림 개수 조회
    public int getUnreadNotificationCount(Long userId, String roleType) {
        String key = getRedisKey(userId, roleType);
        List<Object> notifications = redisTemplate.opsForHash().values(key);

        if (notifications == null) {
            return 0;
        }

        return (int) notifications.stream()
                .filter(notification -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) notification;
                    return map.get("readCheckYn") != null && !(Boolean) map.get("readCheckYn");
                })
                .count();
    }

    public void deleteNotificationById(Long userId, String roleType, String notificationId) {
        String key = getRedisKey(userId, roleType);
        redisTemplate.opsForHash().delete(key, notificationId);
    }
}
