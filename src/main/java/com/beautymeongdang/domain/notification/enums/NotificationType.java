package com.beautymeongdang.domain.notification.enums;

public enum NotificationType {
    RESERVATION("예약 알림"),
    CANCELLATION("예약 취소 알림"),
    QUOTE("견적서 알림"),
    QUOTE_REQUEST("견적서 요청 알림"),
    SHOP_REVIEW("리뷰 알림"),
    CHAT_ROOM("채팅방 생성 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}