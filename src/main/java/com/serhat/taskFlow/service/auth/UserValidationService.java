package com.serhat.taskFlow.service.auth;

import com.serhat.taskFlow.dto.requests.RegisterRequest;
import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.exception.EmailExistException;
import com.serhat.taskFlow.exception.PhoneExistsException;
import com.serhat.taskFlow.exception.UsernameExists;
import com.serhat.taskFlow.interfaces.UserValidationInterface;
import com.serhat.taskFlow.repository.UserRepository;
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
