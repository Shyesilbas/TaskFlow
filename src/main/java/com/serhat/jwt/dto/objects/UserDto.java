package com.serhat.jwt.dto.objects;

import lombok.Builder;

@Builder
public record UserDto(

        String username,
        String email,
        String phone

) {
}
