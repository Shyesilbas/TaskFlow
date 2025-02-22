package com.serhat.jwt.dto.requests;

public record LoginRequest(
        String username,
        String password
) {
}
