package com.serhat.taskFlow.dto.objects;

import com.serhat.taskFlow.entity.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationDto(
        String message,
        NotificationType notificationType,

        LocalDateTime at
) {
}
