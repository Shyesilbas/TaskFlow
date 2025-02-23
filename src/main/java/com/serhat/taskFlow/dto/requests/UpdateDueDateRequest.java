package com.serhat.taskFlow.dto.requests;

public record UpdateDueDateRequest(
        Long taskId,
        String newDate,
        String message
) {
}
