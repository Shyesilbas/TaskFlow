package com.serhat.taskFlow.dto.responses;

import com.serhat.taskFlow.entity.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String username,
        Role role,
        String message
) {
}
