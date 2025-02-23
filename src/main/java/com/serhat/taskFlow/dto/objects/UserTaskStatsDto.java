package com.serhat.taskFlow.dto.objects;

public record UserTaskStatsDto(
        String username,long todo ,long completed, long inProgress, long overdue
) {
}
