package com.serhat.taskFlow.dto.requests;

import com.serhat.taskFlow.entity.enums.RequestStatus;

import java.time.LocalDateTime;

public record TaskChangeRequestDto(
        Long id, Long taskId, String taskTitle, LocalDateTime requestedDueDate,
        RequestStatus status, String adminMessage, String userMessage , LocalDateTime createdAt, LocalDateTime updatedAt
) {
}
