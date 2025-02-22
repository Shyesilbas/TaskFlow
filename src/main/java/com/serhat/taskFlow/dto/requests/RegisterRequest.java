package com.serhat.taskFlow.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2 , max = 20)
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*.]).{6,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
        )
        String password,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,


        @Pattern(regexp = "^\\d{4} \\d{3} \\d{2} \\d{2}$", message = "Invalid phone number format. Expected format: xxxx xxx xx xx")
        String phone
) {
}
