package com.example.rightbackend.noti.controller.dto.response;

import com.example.rightbackend.noti.domain.NotificationCategory;
import com.example.rightbackend.noti.domain.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String content,
        Long relatedId,
        NotificationType noticeType,
        NotificationCategory category,
        Instant createdAt,
        Boolean isRead) {
}
