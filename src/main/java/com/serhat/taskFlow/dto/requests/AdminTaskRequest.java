package com.serhat.taskFlow.dto.requests;

import com.serhat.taskFlow.entity.enums.TaskPriority;

import java.util.List;

public record AdminTaskRequest(
        String title,
        String description,
        TaskPriority taskPriority,
        String status,
        String dueDate,
        Long assignedTo,
        String adminComment,
        List<String> keywords
) {
}
