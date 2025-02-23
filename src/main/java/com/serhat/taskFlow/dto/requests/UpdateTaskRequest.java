package com.serhat.taskFlow.dto.requests;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
        String title,
        String description,
        String status,
        String dueDate
) {
}
