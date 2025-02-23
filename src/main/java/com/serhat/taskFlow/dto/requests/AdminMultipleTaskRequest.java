package com.serhat.taskFlow.dto.requests;

import com.serhat.taskFlow.entity.enums.TaskPriority;

import java.util.List;

public record AdminMultipleTaskRequest(
        String title,
        String description,
        String status,
        String adminComment,
        TaskPriority taskPriority,
        String dueDate,
        List<Long> assignedTo,
        List<String> keywords
) {
}
