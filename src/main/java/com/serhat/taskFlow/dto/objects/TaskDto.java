package com.serhat.taskFlow.dto.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serhat.taskFlow.entity.enums.TaskPriority;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TaskDto(
        Long taskId,
        String title,
        String assignedBy,
        String description,
        String status,
        String userComment,
        String adminComment,
        TaskPriority taskPriority,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
        LocalDateTime dueDate,
        String assignedToUsername,
        List<String> keywords
) {
}
