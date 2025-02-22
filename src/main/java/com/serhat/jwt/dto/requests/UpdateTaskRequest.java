package com.serhat.jwt.dto.requests;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
        String title,
        String description,
        String status,
        LocalDateTime dueDate
) {
}
