package com.serhat.taskFlow.dto.requests;

public record AdminTaskRequest(
        String title,
        String description,
        String status,
        String dueDate,
        Long assignedTo
) {
}
