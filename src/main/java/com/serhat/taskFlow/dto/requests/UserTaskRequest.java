package com.serhat.taskFlow.dto.requests;

import java.util.List;

public record UserTaskRequest(
        String title,
        String description,
        String status,
        String dueDate,
        List<String> keywords
) {
}
