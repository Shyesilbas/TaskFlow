package com.serhat.jwt.service.auth;

import com.serhat.jwt.dto.requests.LoginRequest;
import com.serhat.jwt.dto.requests.RegisterRequest;
import com.serhat.jwt.dto.responses.AuthResponse;
import com.serhat.jwt.dto.responses.RegisterResponse;
import com.serhat.jwt.entity.AppUser;
import com.serhat.jwt.entity.enums.Role;
import com.serhat.jwt.exception.InvalidTokenException;
import com.serhat.jwt.exception.TokenNotFoundException;
import com.serhat.jwt.interfaces.PasswordValidationInterface;
import com.serhat.jwt.interfaces.UserValidationInterface;
import com.serhat.jwt.jwt.JwtUtil;
import com.serhat.jwt.jwt.TokenBlacklistService;
import com.serhat.jwt.mapper.AuthMapper;
import com.serhat.jwt.mapper.UserMapper;
import com.serhat.jwt.repository.UserRepository;
import com.serhat.jwt.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;
    private final AuthMapper authMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordValidationInterface passwordValidationInterface;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidationInterface userValidationInterface;
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        userValidationInterface.validateUserRegistration(request);

        AppUser user = userMapper.toUser(request);
        userRepository.save(user);

        return new RegisterResponse(
                "Register Successful! Now you can login with your credentials.",
                user.getUsername(),
                user.getEmail(),
                user.getMembershipPlan(),
                LocalDateTime.now()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        passwordValidationInterface.validatePassword(request.password(), userDetails.getPassword());

        String token = jwtUtil.generateToken(userDetails);
        jwtUtil.saveUserToken(userDetails, token);
        Role role = jwtUtil.extractRole(token);


        log.info("Login successful for user: {}", request.username());
        return authMapper.createAuthResponse(token, userDetails.getUsername(), role, "Login Successful!");
    }

    @Transactional
    public AuthResponse logout(HttpServletRequest request) {
        log.info("Processing logout request");

        try {
            String jwtToken = jwtUtil.getTokenFromAuthorizationHeader(request);
            jwtUtil.invalidateToken(jwtToken);
            blacklistService.blacklistToken(jwtToken);

            String username = jwtUtil.extractUsername(jwtToken);
            Role role = jwtUtil.extractRole(jwtToken);
            log.info("Logout successful for user: {}", username);

            return authMapper.createAuthResponse(jwtToken, username, role, "Logout successful");
        } catch (InvalidTokenException | TokenNotFoundException e) {
            log.warn("Logout failed: Invalid or not found token - {}", e.getMessage());
            throw new RuntimeException("Invalid or not found token");
        }
    }

}