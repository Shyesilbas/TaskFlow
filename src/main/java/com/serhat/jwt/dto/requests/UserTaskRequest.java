package com.serhat.jwt.dto.requests;

public record UserTaskRequest(
        String title,
        String description,
        String status,
        String dueDate
) {
}
