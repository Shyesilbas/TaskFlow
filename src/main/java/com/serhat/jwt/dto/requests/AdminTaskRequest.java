package com.serhat.jwt.dto.requests;

public record AdminTaskRequest(
        String title,
        String description,
        String status,
        String dueDate,
        Long assignedTo
) {
}
