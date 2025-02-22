package com.serhat.jwt.mapper;

import com.serhat.jwt.dto.responses.AuthResponse;
import com.serhat.jwt.entity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    public AuthResponse createAuthResponse(String token, String username, Role role, String message) {
        return AuthResponse.builder()
                .token(token)
                .username(username)
                .role(role)
                .message(message)
                .build();
    }
}
