package com.serhat.taskFlow.dto.requests;

public record UserTaskRequest(
        String title,
        String description,
        String status,
        String dueDate
) {
}
