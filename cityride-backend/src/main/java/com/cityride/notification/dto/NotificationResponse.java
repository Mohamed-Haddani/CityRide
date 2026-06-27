package com.cityride.notification.dto;

import com.cityride.notification.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String message,
        Long referenceId,
        boolean read,
        Instant createdAt
) {
}
