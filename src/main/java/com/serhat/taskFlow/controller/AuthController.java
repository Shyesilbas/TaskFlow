package com.serhat.taskFlow.controller;

import com.serhat.taskFlow.dto.requests.LoginRequest;
import com.serhat.taskFlow.dto.requests.RegisterRequest;
import com.serhat.taskFlow.dto.responses.AuthResponse;
import com.serhat.taskFlow.dto.responses.RegisterResponse;
import com.serhat.taskFlow.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest servletRequest){
        return ResponseEntity.ok(authService.logout(servletRequest));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminTest(){
        return "Only admin can access";
    }
}
