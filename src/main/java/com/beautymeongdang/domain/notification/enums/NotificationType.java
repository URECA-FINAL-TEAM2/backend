package com.beautymeongdang.domain.notification.enums;

public enum NotificationType {
    RESERVATION("예약 알림"),
    CANCELLATION("예약 취소 알림"),
    QUOTE("견적서 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}