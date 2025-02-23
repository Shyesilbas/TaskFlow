package com.serhat.taskFlow.dto.requests;

public record AddUserCommentRequest(
        String comment,
        Long taskId
) {
}
