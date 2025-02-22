package com.serhat.taskFlow.dto.objects;

import lombok.Builder;

@Builder
public record UserDto(

        String username,
        String email,
        String phone

) {
}
