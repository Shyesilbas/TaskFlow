package com.serhat.jwt.mapper;

import com.serhat.jwt.dto.requests.RegisterRequest;
import com.serhat.jwt.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public AppUser toUser(RegisterRequest request){
        return AppUser.builder()
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .build();
    }

}
