package com.serhat.jwt.dto.objects;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TaskDto(
        Long taskId,
        String title,
        String assignedBy,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime dueDate,
        String assignedToUsername
) {
}
