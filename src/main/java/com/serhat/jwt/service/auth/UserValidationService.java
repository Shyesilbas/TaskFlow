package com.serhat.jwt.service.auth;

import com.serhat.jwt.dto.requests.RegisterRequest;
import com.serhat.jwt.entity.AppUser;
import com.serhat.jwt.exception.EmailExistException;
import com.serhat.jwt.exception.PhoneExistsException;
import com.serhat.jwt.exception.UsernameExists;
import com.serhat.jwt.interfaces.UserValidationInterface;
import com.serhat.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService implements UserValidationInterface {
    private final UserRepository userRepository;
    @Override
    public void validateUserRegistration(RegisterRequest request) {
        Optional<AppUser> existingUser = userRepository.findByEmailOrUsernameOrPhone(
                request.email(),
                request.username(),
                request.phone()
        );

        existingUser.ifPresent(user -> {
            if (user.getEmail().equals(request.email())) {
                log.warn("Registration failed: Email already exists - {}", request.email());
                throw new EmailExistException("Email already exists!");
            }
            if (user.getUsername().equals(request.username())) {
                log.warn("Registration failed: Username already exists - {}", request.username());
                throw new UsernameExists("Username already exists!");
            }
            if (user.getPhone().equals(request.phone())) {
                log.warn("Registration failed: Phone number already exists - {}", request.phone());
                throw new PhoneExistsException("Phone number already exists!");
            }
        });
    }
}
