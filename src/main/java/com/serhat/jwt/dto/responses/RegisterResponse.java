package com.serhat.jwt.dto.responses;

import com.serhat.jwt.entity.enums.MembershipPlan;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RegisterResponse(
        String message,
        String username,
        String email ,
        MembershipPlan membershipPlan,
        LocalDateTime registerDate
) {
}
