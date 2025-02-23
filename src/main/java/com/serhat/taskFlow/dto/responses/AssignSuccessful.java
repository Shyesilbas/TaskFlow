package com.serhat.taskFlow.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AssignSuccessful(
        String message,
        Long userId,
        Long adminId,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
        LocalDateTime assignDate
) {
}
