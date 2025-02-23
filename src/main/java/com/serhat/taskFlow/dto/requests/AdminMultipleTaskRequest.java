package com.serhat.taskFlow.dto.requests;

import java.util.List;

public record AdminMultipleTaskRequest(
        String title,
        String description,
        String status,
        String dueDate,
        List<Long> assignedTo,
        List<String> keywords
) {
}
