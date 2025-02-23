package com.serhat.taskFlow.dto.objects;

public record TaskStatsDto(
        long todo,
        long inProgress,
        long completed,

        long overdue
) {
}
