package com.serhat.taskFlow.dto.responses;

import com.serhat.taskFlow.entity.enums.MembershipPlan;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RegisterResponse(
        String message,
        String username,
        String email ,
        LocalDateTime registerDate
) {
}
