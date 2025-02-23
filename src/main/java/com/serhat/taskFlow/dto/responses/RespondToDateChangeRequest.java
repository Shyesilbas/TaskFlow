package com.serhat.taskFlow.dto.responses;

public record RespondToDateChangeRequest(
        Long requestId,
        boolean approved,
        String adminMessage
) {
}
