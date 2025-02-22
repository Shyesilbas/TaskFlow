package com.serhat.jwt.dto.responses;

import com.serhat.jwt.entity.enums.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String username,
        Role role,
        String message
) {
}
