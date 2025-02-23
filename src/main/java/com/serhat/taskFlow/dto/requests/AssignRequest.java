package com.serhat.taskFlow.dto.requests;

public record AssignRequest(
        Long userId,
        Long adminId
) {
}
