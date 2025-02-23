package com.serhat.taskFlow.dto.requests;

import java.util.List;

public record AdminTaskRequest(
        String title,
        String description,
        String status,
        String dueDate,
        Long assignedTo,
        List<String> keywords
) {
}
